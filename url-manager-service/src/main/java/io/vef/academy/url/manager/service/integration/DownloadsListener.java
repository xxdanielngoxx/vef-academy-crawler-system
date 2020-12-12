package io.vef.academy.url.manager.service.integration;

import io.vef.academy.common.events.UrlDownloadFailedEvent;
import io.vef.academy.common.events.UrlDownloadSucceedEvent;
import io.vef.academy.url.manager.service.services.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import topics.DownloadServiceTopic;

@Slf4j
@Component
@KafkaListener(
        groupId = "url-manager-downloads-listener",
        topics = DownloadServiceTopic.DOWNLOADS,
        containerFactory = "downloadsKafkaListenerContainerFactory"
)
public class DownloadsListener {

    private final UrlService urlService;

    public DownloadsListener(UrlService urlService) {
        this.urlService = urlService;
    }

    @KafkaHandler
    public void on(UrlDownloadSucceedEvent event) {
        this.urlService.handleDownloadSucceed(event);
    }

    @KafkaHandler
    public void on(UrlDownloadFailedEvent event) {
        this.urlService.handleDownloadFailedEvent(event);
    }

    @KafkaHandler(isDefault = true)
    public void on(Object event) {
        log.info("Default event handler, event: {}", event);
    }
}
