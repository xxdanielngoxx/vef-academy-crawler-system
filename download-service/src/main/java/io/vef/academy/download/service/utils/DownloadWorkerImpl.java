package io.vef.academy.download.service.utils;

import io.vef.academy.common.events.UrlDownloadFailedEvent;
import io.vef.academy.common.events.UrlDownloadSucceedEvent;
import io.vef.academy.download.service.domain.Download;
import io.vef.academy.download.service.repositories.DownloadRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import topics.DownloadServiceTopic;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class DownloadWorkerImpl implements DownloadWorker {

    private final Executor taskExecutor;

    private final DownloadRepository downloadRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DownloadWorkerImpl(Executor taskExecutor, DownloadRepository downloadRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.taskExecutor = taskExecutor;
        this.downloadRepository = downloadRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CompletableFuture downloadContent(String id, String url, String taskId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document document = Jsoup.connect(url).timeout(5000).get();
                String content = document.body().html();
                return Optional.of(content);
            } catch (IOException e) {
                log.error("Download Failed {url: {}}, caused: {}", url, e.getCause());
                return Optional.empty();
            }
        }, taskExecutor)
                .thenApplyAsync(
                        result -> result
                                .map(content -> this.saveContentAndPublishEventWhenSuccesses(id, url, taskId, (String) content))
                                .orElseGet(() -> this.markDownloadFailedAndPublishEventWhenFailed(id, url, taskId)), taskExecutor);
    }

    @Transactional
    private Optional<Download> saveContentAndPublishEventWhenSuccesses(String id, String url, String taskId, String content) {
        Optional<Download> optionalDownload = this.downloadRepository.findById(id);
        if (optionalDownload.isPresent()) {
            Download downloading = optionalDownload.get();
            Optional<Download> targetDownload = downloading.markDownloaded(content);
            if (targetDownload.isPresent()) {
                Download updatedDownload = this.downloadRepository.save(targetDownload.get());
                this.kafkaTemplate.send(
                        DownloadServiceTopic.DOWNLOADS,
                        url,
                        UrlDownloadSucceedEvent.of(id, url, taskId)
                );
            }
            return targetDownload;
        }
        throw new EntityNotFoundException("Not found Download entity with id: " + id);
    }

    @Transactional
    private Optional<Download> markDownloadFailedAndPublishEventWhenFailed(String id, String url, String taskId) {
        Optional<Download> optionalDownload = this.downloadRepository.findById(id);
        if (optionalDownload.isPresent()) {
            Download downloading = optionalDownload.get();
            Optional<Download> targetDownload = downloading.markFailed();
            if (targetDownload.isPresent()) {
                Download updatedDownload = this.downloadRepository.save(targetDownload.get());
                this.kafkaTemplate.send(
                        DownloadServiceTopic.DOWNLOADS,
                        url,
                        UrlDownloadFailedEvent.of(id, url, taskId, updatedDownload.getFailedTimes())
                );
            }
            return targetDownload;
        }
        throw new EntityNotFoundException("Not found Download entity with id: " + id);
    }
}
