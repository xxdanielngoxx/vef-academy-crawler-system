package io.vef.academy.download.service.services;

import io.vef.academy.download.service.domain.Download;
import io.vef.academy.download.service.repositories.DownloadRepository;
import io.vef.academy.download.service.utils.DownloadWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
public class DownloadServiceImpl implements DownloadService {

    private final DownloadWorker downloadWorker;

    private final DownloadRepository downloadRepository;

    public DownloadServiceImpl(DownloadWorker downloadWorker, DownloadRepository downloadRepository) {
        this.downloadWorker = downloadWorker;
        this.downloadRepository = downloadRepository;
    }

    @Override
    public Download getDownloadById(String id) {
        return this.downloadRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("Download not found by id: {}", id);
                    return new RuntimeException("Download not found by id: " + id);
                });
    }

    @Override
    public Optional<Download> getDownloadByUrlAndTaskId(String url, String taskId) {
        return this.downloadRepository.findByUrlAndTaskId(url, taskId);
    }

    @Override
    public Optional<Download> downloadUrl(String url, String taskId) {
        Optional<Download> optionalDownload = this.getDownloadByUrlAndTaskId(url, taskId);
        if (!optionalDownload.isPresent()) {
            Download downloading = this.createNewDownload(url, taskId);
            return Optional.of(downloading);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Download> retryDownloadUrl(String id) {
        Download targetDownload = this.getDownloadById(id);
        Optional<Download> retriedDownload = targetDownload.retry();
        if (retriedDownload.isPresent()) {
            Download persistedDownload = this.downloadRepository.save(retriedDownload.get());
            this.downloadWorker.downloadContent(
                    persistedDownload.getId(),
                    persistedDownload.getUrl(),
                    persistedDownload.getTaskId()
            );
        }
        return retriedDownload;
    }

    @Transactional
    private Download createNewDownload(String url, String taskId) {
        Download newDownload = Download.newDownload(url, taskId);
        Download downloading = this.downloadRepository.save(newDownload);
        this.downloadWorker.downloadContent(downloading.getId(), url, taskId);
        return downloading;
    }
}
