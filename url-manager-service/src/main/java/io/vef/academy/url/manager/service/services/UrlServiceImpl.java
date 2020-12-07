package io.vef.academy.url.manager.service.services;

import io.vef.academy.common.events.TaskExecutedEvent;
import io.vef.academy.common.events.UrlDispatchedEvent;
import io.vef.academy.url.manager.service.domain.*;
import io.vef.academy.url.manager.service.repositories.SeedUrlRepository;
import io.vef.academy.url.manager.service.repositories.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import topics.UrlManagerTopic;

import java.util.Optional;

@Slf4j
@Component
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    private final SeedUrlRepository seedUrlRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UrlServiceImpl(
            UrlRepository urlRepository,
            SeedUrlRepository seedUrlRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.urlRepository = urlRepository;
        this.seedUrlRepository = seedUrlRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public Url handleUrlDispatched(UrlDispatchedEvent urlDispatchedEvent) {
       return null;
    }

    @Override
    public void handleTaskExecuted(TaskExecutedEvent taskExecutedEvent) {
        taskExecutedEvent.getSeedUrlList()
                .stream()
                .forEach(seedUrl -> {
                        SeedUrl persistedSeedUrl = this.saveSeedUrl(seedUrl.getId(), seedUrl.getUrl());
                        this.dispatchUrl(seedUrl.getUrl(), persistedSeedUrl, taskExecutedEvent.getTaskId());
                });
    }

    private void dispatchUrl(String url, SeedUrl seedUrl, String taskId) {
        Optional<?> optionalUrlDetailsId = this.urlRepository.findByUrl(url)
                .map(existedUrl -> {
                    return this.processSavingDispatch(existedUrl, seedUrl, taskId);
                })
                .orElseGet(() -> {
                    Url newUrl = Url.newUrl(url, seedUrl);
                    return this.processSavingDispatch(newUrl, seedUrl, taskId);
                });
        optionalUrlDetailsId.ifPresent(urlDetailsId -> {
            this.kafkaTemplate.send(
                    UrlManagerTopic.URLS,
                    url,
                    UrlDispatchedEvent.of(
                            (String) urlDetailsId,
                            url,
                            new seed.SeedUrl(seedUrl.getId(), seedUrl.getUrl()),
                            taskId
                    )
            );
        });
    }

    @Transactional
    private SeedUrl saveSeedUrl(String id, String url) {
        Optional<SeedUrl> optionalSeedUrl = this.seedUrlRepository.findById(id);
        return optionalSeedUrl.orElseGet(() -> this.seedUrlRepository.save(SeedUrl.of(id, url)));
    }

    @Transactional
    private Optional<Pair<UrlDetails, Url>> addUrlDetails(Url url, String taskId) {
        Optional<UrlDetails> optionalUrlDetails = url.addUrlDetails(taskId);
        if (optionalUrlDetails.isPresent()) {
            Url persistedUrl = this.urlRepository.save(url);
            return Optional.of(Pair.of(optionalUrlDetails.get(), persistedUrl));
        }
        return Optional.empty();
    }

    @Transactional
    private Optional<Pair<SeedDetails, Url>> addSeedUrl(Url url, SeedUrl seedUrl) {
        Optional<SeedDetails> addSeedUrlResult = url.addSeedUrl(seedUrl);
        if (addSeedUrlResult.isPresent()) {
            Url persistedUrl = this.urlRepository.save(url);
            return Optional.of(Pair.of(addSeedUrlResult.get(), persistedUrl));
        }
        return Optional.empty();
    }

    @Transactional
    private Optional<String> processSavingDispatch(Url url, SeedUrl seedUrl, String taskId) {
        Optional<Pair<UrlDetails, Url>> addUrlDetailsResult = this.addUrlDetails(url, taskId);
        if (addUrlDetailsResult.isPresent()) {
            this.addSeedUrl(url, seedUrl);
            return Optional.of(addUrlDetailsResult.get().getFirst().getId());
        }
        return Optional.empty();
    }
}
