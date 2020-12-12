package io.vef.academy.url.manager.service.services;

import io.vef.academy.common.events.*;
import io.vef.academy.url.manager.service.domain.*;
import io.vef.academy.url.manager.service.repositories.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import seed.SeedUrl;
import topics.UrlManagerTopic;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class UrlServiceImpl implements UrlService {

    @Value("${io.vef.academy.download.retry.limit}")
    private int retryLimit;

    private final UrlRepository urlRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UrlServiceImpl(
            UrlRepository urlRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.urlRepository = urlRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @Override
    public void handleTaskExecuted(TaskExecutedEvent event) {
        for (SeedUrl seedUrl : event.getSeedUrlList()) {
            this.dispatchUrl(seedUrl.getUrl(), event.getTaskId(), seedUrl.getId());
        }
    }

    @Transactional
    @Override
    public Optional<UrlDownloadDetails> handleDownloadSucceed(UrlDownloadSucceedEvent event) {
        Url targetUrl = this.getUrlById(event.getUrl());

        Optional<UrlDownloadDetails> targetDownloadDetails = targetUrl
                .markUrlDownloadDetailsAsDownloaded(event.getTaskId(), event.getId());

        if (targetDownloadDetails.isPresent()) {
            Url persistedUrl = this.urlRepository.save(targetUrl);
            log.info("Persisted Url: {}", persistedUrl);
            this.publishEventUrlDownloaded(event.getUrl(), event.getTaskId(), event.getId());
        }

        return targetDownloadDetails;
    }

    @Transactional
    @Override
    public Optional<UrlDownloadDetails> handleDownloadFailedEvent(UrlDownloadFailedEvent event) {
        if (event.getFailedTimes() <= this.retryLimit) {
            return this.retryDownload(event.getUrl(), event.getTaskId(), event.getId());
        }
        return this.markDownloadFailed(event.getUrl(), event.getTaskId());
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
        log.debug("Input: {url: {}, taskId: {}, seedId: {}}", url, taskId, seedId);
        Optional<Url> optionalUrl = this.urlRepository
                .findById(url);

        if (optionalUrl.isPresent()) {
            return this.dispatchExistedUrl(optionalUrl.get(), taskId, seedId);
        }
        return this.dispatchNewUrl(url, taskId, seedId);
    }

    @Transactional
    private Optional<Url> dispatchExistedUrl(Url existedUrl, String taskId, String seedId) {
        Optional<UrlDownloadDetails> optionalUrlDownloadDetails = existedUrl.addUrlDownloadDetails(taskId, seedId);
        if (optionalUrlDownloadDetails.isPresent()) {
            Url persistedUrl = this.urlRepository.save(existedUrl);
            log.info("Persisted Url: {}", persistedUrl);
            this.publishEventUrlDispatched(existedUrl.getUrl(), taskId);
            return Optional.of(persistedUrl);
        }
        log.info("Url Already Dispatched, {url: {}, taskId: {}, seedId: {}}", existedUrl.getUrl(), taskId, seedId);
        return Optional.empty();
    }

    @Transactional
    private Optional<Url> dispatchNewUrl(String url, String taskId, String seedId) {
        Url urlEntity = Url.newUrl(url, taskId, seedId);
        Url persistedUrl = this.urlRepository.save(urlEntity);
        log.info("Persisted Url: {}", persistedUrl);
        this.publishEventUrlDispatched(persistedUrl.getUrl(), taskId);
        return Optional.of(persistedUrl);
    }

    @Transactional
    private Optional<UrlDownloadDetails> retryDownload(String url, String taskId, String downloadId) {
        Url targetUrl = this.getUrlById(url);
        Optional<UrlDownloadDetails> targetDownloadDetails = targetUrl.markUrlDownloadDetailsAsRetrying(taskId);
        if (targetDownloadDetails.isPresent()) {
            Url persistedUrl = this.urlRepository.save(targetUrl);
            log.info("Persisted Url After Mark Retrying: {}", persistedUrl);
            this.publishEventUrlRetryingDownload(url, taskId, downloadId);
        }
        return targetDownloadDetails;
    }

    @Transactional
    private Optional<UrlDownloadDetails> markDownloadFailed(String url, String taskId) {
        Url targetUrl = this.getUrlById(url);
        Optional<UrlDownloadDetails> targetDownloadDetails = targetUrl.markUrlDownloadDetailsAsRetrying(taskId);
        if (targetDownloadDetails.isPresent()) {
            Url persistedUrl = this.urlRepository.save(targetUrl);
            log.info("Persisted Url After Mark Failed: {}", persistedUrl);
        }
        return targetDownloadDetails;
    }

    private void publishEventUrlRetryingDownload(String url, String taskId, String downloadId) {
        UrlDownloadRetriedEvent event = UrlDownloadRetriedEvent.of(url, taskId, downloadId);
        this.kafkaTemplate.send(UrlManagerTopic.URLS, url, event);
        log.info("Published event Url Retried: {}", event);
    }

    private void publishEventUrlDispatched(String url, String taskId) {
        UrlDispatchedEvent event = UrlDispatchedEvent.of(url, taskId);
        this.kafkaTemplate.send(UrlManagerTopic.URLS, url, event);
        log.info("Published event Url Dispatched: {}", event);
    }

    private void publishEventUrlDownloaded(String url, String taskId, String downloadedId) {
        Set<String> seedIds = this.getUrlById(url).getAllSeedId(taskId);
        UrlDownloadedEvent event = UrlDownloadedEvent.of(url, taskId, downloadedId, seedIds);
        this.kafkaTemplate.send(UrlManagerTopic.URLS, url, event);
        log.info("Published event Url Downloaded: {}", event);
    }
}
