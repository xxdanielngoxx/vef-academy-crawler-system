package io.vef.academy.download.service.utils;

import io.vef.academy.common.events.ContentDownloadFailedEvent;
import io.vef.academy.common.events.ContentDownloadedEvent;
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
                Document document = Jsoup.connect(url).get();
                String content = document.body().html();
                return Optional.of(content);
            } catch (IOException e) {
                log.error("Download Failed {url: {}}, caused: {}", url, e.getCause());
                return Optional.empty();
            }
        }, taskExecutor).thenApplyAsync(result -> {
           if (result.isPresent()) {
               Download downloaded = this.saveContentAndPublishEventWhenSuccesses(
                       id,
                       url,
                       taskId,
                       (String)  result.get()
               );
               return Optional.of(downloaded);
           } else {
              Download failed = this.markDownloadFailedAndPublishEventWhenFailed(id, url, taskId);
              return Optional.of(failed);
           }
        }, taskExecutor);
    }

    @Transactional
    private Download saveContentAndPublishEventWhenSuccesses(String id, String url, String taskId, String content) {
        Optional<Download> optionalDownload = this.downloadRepository.findById(id);
        if (optionalDownload.isPresent()) {
            Download downloading = optionalDownload.get();
            Download downloaded = downloading.markDownloaded(content);
            Download updatedDownload = this.downloadRepository.save(downloaded);
            this.kafkaTemplate.send(
                    DownloadServiceTopic.DOWNLOADS,
                    url,
                    ContentDownloadedEvent.of(id, url, taskId)
            );
            return updatedDownload;
        }
        throw new EntityNotFoundException("Not found Download entity with id: " + id);
    }

    @Transactional
    private Download markDownloadFailedAndPublishEventWhenFailed(String id, String url, String taskId) {
        Optional<Download> optionalDownload = this.downloadRepository.findById(id);
        if (optionalDownload.isPresent()) {
            Download downloading = optionalDownload.get();
            Download failed = downloading.markFailed();
            Download updatedDownload = this.downloadRepository.save(failed);
            this.kafkaTemplate.send(
                    DownloadServiceTopic.DOWNLOADS,
                    url,
                    ContentDownloadFailedEvent.of(id, url, taskId, updatedDownload.getFailedTimes())
            );
            return updatedDownload;
        }
        throw new EntityNotFoundException("Not found Download entity with id: " + id);
    }
}
