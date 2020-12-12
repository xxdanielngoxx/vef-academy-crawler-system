package io.vef.academy.download.service.integration;

import io.vef.academy.common.events.UrlDispatchedEvent;
import io.vef.academy.common.events.UrlDownloadRetriedEvent;
import io.vef.academy.download.service.services.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import topics.UrlManagerTopic;

@Slf4j
@Component
@KafkaListener(
        id = "download-url-manager-listener",
        topics = UrlManagerTopic.URLS,
        containerFactory = "urlManagerKafkaListenerContainerFactory"
)
public class UrlManagerListener {

    private final DownloadService downloadService;

    public UrlManagerListener(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @KafkaHandler
    public void on(UrlDispatchedEvent urlDispatchedEvent) {
        this.downloadService.downloadUrl(urlDispatchedEvent.getUrl(), urlDispatchedEvent.getTaskId());
    }

    public void on(UrlDownloadRetriedEvent event, Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
    }

    @KafkaHandler(isDefault = true)
    public void on(Object event) {
        log.info("Received Message: {}", event);
    }
}
