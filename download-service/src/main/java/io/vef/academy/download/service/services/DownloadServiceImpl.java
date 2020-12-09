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
public class DownloadServiceImpl implements DownloadService{

    private final DownloadWorker downloadWorker;

    private final DownloadRepository downloadRepository;

    public DownloadServiceImpl(DownloadWorker downloadWorker, DownloadRepository downloadRepository) {
        this.downloadWorker = downloadWorker;
        this.downloadRepository = downloadRepository;
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

    @Transactional
    private Download createNewDownload(String url, String taskId) {
        Download newDownload = Download.newDownload(url, taskId);
        Download downloading = this.downloadRepository.save(newDownload);
        this.downloadWorker.downloadContent(downloading.getId(), url, taskId);
        return downloading;
    }
}
