package io.vef.academy.download.service.utils;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public interface DownloadWorker {
    CompletableFuture<Optional<String>> downloadContent(String id, String url, String taskId);
}
