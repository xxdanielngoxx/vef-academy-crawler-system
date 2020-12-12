package io.vef.academy.extractor.service.services;

import io.vef.academy.common.events.UrlDownloadedEvent;
import io.vef.academy.extractor.service.domain.TargetUrl;
import io.vef.academy.extractor.service.repositories.TargetUrlRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import responses.DownloadedContentResponse;

import java.util.Optional;

@Component
public class TargetUrlServiceImpl implements TargetUrlService {

    private final TargetUrlRepository targetUrlRepository;

    public TargetUrlServiceImpl(TargetUrlRepository targetUrlRepository) {
        this.targetUrlRepository = targetUrlRepository;
    }

    @Override
    public void handleUrlDownloadedEvent(UrlDownloadedEvent event) {
        TargetUrl targetUrl = this.createTargetUrl(event.getUrl(), event.getTaskId(), event.getDownloadedId());
        this.cloneTargetUrl(targetUrl, event.getDownloadedId());
    }

    private TargetUrl createTargetUrl(String url, String taskId, String downloadedId) {
        Optional<TargetUrl> optionalTargetUrl = this.targetUrlRepository.findByUrlAndTaskId(url, taskId);
        return optionalTargetUrl.orElseGet(() -> {
            TargetUrl targetUrl = TargetUrl.of(url, taskId, downloadedId);
            TargetUrl persistedTargetUrl = this.targetUrlRepository.save(targetUrl);
            return persistedTargetUrl;
        });
    }

    private void cloneTargetUrl(TargetUrl targetUrl, String downloadedId) {
        WebClient
                .builder()
                .baseUrl("http://localhost:8082/api/v1")
                .build()
                .get()
                .uri("/downloads/" + downloadedId)
                .retrieve()
                .bodyToMono(DownloadedContentResponse.class)
                .subscribe(downloadedContentResponse -> {
                    Optional<TargetUrl> optionalTargetUrl = targetUrl.cloneContent(downloadedContentResponse.getContent());
                    if (optionalTargetUrl.isPresent()) {
                        TargetUrl persistedTargetUrl = this.targetUrlRepository.save(optionalTargetUrl.get());
                    }
                });
    }
}
