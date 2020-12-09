package io.vef.academy.url.manager.service.services;

import io.vef.academy.common.events.TaskExecutedEvent;
import io.vef.academy.common.events.UrlDispatchedEvent;
import io.vef.academy.common.events.UrlDownloadFailedEvent;
import io.vef.academy.common.events.UrlDownloadSucceedEvent;
import io.vef.academy.url.manager.service.domain.*;
import io.vef.academy.url.manager.service.repositories.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import seed.SeedUrl;
import topics.UrlManagerTopic;

import java.util.Optional;

@Slf4j
@Component
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UrlServiceImpl(
            UrlRepository urlRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.urlRepository = urlRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void handleTaskExecuted(TaskExecutedEvent event) {
        for (SeedUrl seedUrl : event.getSeedUrlList()) {
            this.dispatchUrl(seedUrl.getUrl(), event.getTaskId(), seedUrl.getId());
        }
    }

    @Override
    public Optional<UrlDownloadDetails> handleDownloadSucceed(UrlDownloadSucceedEvent event) {
        Url url = this.getUrlById(event.getUrl());

        Optional<UrlDownloadDetails> optionalUrlDownloadDetails = url
                .markUrlDownloadDetailsAsDownloaded(event.getTaskId(), event.getId());

        boolean isModified = optionalUrlDownloadDetails.isPresent();

        if (isModified) {
            Url persistedUrl = this.urlRepository.save(url);
            log.info("Persisted Url: {}", persistedUrl);
            return optionalUrlDownloadDetails;
        }

        return Optional.empty();
    }

    @Override
    public Optional<UrlDownloadDetails> handleDownloadFailedEvent(UrlDownloadFailedEvent event) {
        Url url = this.getUrlById(event.getUrl());


        Optional<UrlDownloadDetails> optionalUrlDownloadDetails = url
                .markUrlDownloadDetailsAsFailed(event.getTaskId());

        boolean isModified = optionalUrlDownloadDetails.isPresent();

        if (isModified) {
            Url persistedUrl = this.urlRepository.save(url);
            log.info("Persisted Url: {}", persistedUrl);
            return optionalUrlDownloadDetails;
        }

        return Optional.empty();
    }

    @Override
    public Url getUrlById(String id) {
        return this.urlRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("Url not found: {}", id);
                    return new RuntimeException("Url not found: " + id);
                });
    }

    @Transactional
    private Optional<Url> dispatchUrl(String url, String taskId, String seedId) {
        log.debug("Input: {url: {}, taskId: {}, seedid: {}}", url, taskId, seedId);
        Optional<Url> optionalUrl = this.urlRepository
                .findById(url);

        if (optionalUrl.isPresent()) {
            return this.dispatchExistedUrl(optionalUrl.get(), taskId, seedId);
        }
        return this.dispatchNewUrl(url, taskId, seedId);
    }


    private Optional<Url> dispatchExistedUrl(Url existedUrl, String taskId, String seedId) {
        Optional<UrlDownloadDetails> optionalUrlDownloadDetails = existedUrl.addUrlDownloadDetails(taskId, seedId);
        if (optionalUrlDownloadDetails.isPresent()) {
            Url persistedUrl = this.urlRepository.save(existedUrl);
            log.info("Persisted Url: {}", persistedUrl);
            this.publishEventUrlDispatched(existedUrl.getUrl(),taskId);
            return Optional.of(persistedUrl);
        }
        log.info("Url Already Dispatched, {url: {}, taskId: {}, seedId: {}}", existedUrl.getUrl(), taskId, seedId);
        return Optional.empty();
    }

    private Optional<Url> dispatchNewUrl(String url, String taskId, String seedId) {
        Url urlEntity = Url.newUrl(url, taskId, seedId);
        Url persistedUrl = this.urlRepository.save(urlEntity);
        log.info("Persisted Url: {}", persistedUrl);
        this.publishEventUrlDispatched(persistedUrl.getUrl(), taskId);
        return Optional.of(persistedUrl);
    }

    private void publishEventUrlDispatched(String url, String taskId) {
        UrlDispatchedEvent event = UrlDispatchedEvent.of(url, taskId);
        this.kafkaTemplate.send(UrlManagerTopic.URLS, url, event);
        log.info("Published event Url Dispatched: {}", UrlDispatchedEvent.of(url, taskId));
    }
}
