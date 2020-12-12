package io.vef.academy.download.service.services;

import io.vef.academy.download.service.domain.Download;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface DownloadService {
    Download getDownloadById(String id);
    Optional<Download> getDownloadByUrlAndTaskId(String url, String taskId);
    Optional<Download> downloadUrl(String url, String taskId);
    Optional<Download> retryDownloadUrl(String id);
}
