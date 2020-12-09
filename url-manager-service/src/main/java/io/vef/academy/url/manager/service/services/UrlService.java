package io.vef.academy.url.manager.service.services;

import io.vef.academy.common.events.TaskExecutedEvent;
import io.vef.academy.common.events.UrlDownloadFailedEvent;
import io.vef.academy.common.events.UrlDownloadSucceedEvent;
import io.vef.academy.url.manager.service.domain.Url;
import io.vef.academy.url.manager.service.domain.UrlDownloadDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UrlService {
    void handleTaskExecuted(TaskExecutedEvent event);
    Optional<UrlDownloadDetails> handleDownloadSucceed(UrlDownloadSucceedEvent event);
    Optional<UrlDownloadDetails> handleDownloadFailedEvent(UrlDownloadFailedEvent event);
    Url getUrlById(String id);
}
