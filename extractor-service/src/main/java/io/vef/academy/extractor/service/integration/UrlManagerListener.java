package io.vef.academy.extractor.service.integration;

import io.vef.academy.common.events.UrlDownloadedEvent;
import io.vef.academy.extractor.service.services.TargetUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import topics.UrlManagerTopic;

@Slf4j
@Component
@KafkaListener(
        groupId = "extractor-url-manager-listener",
        topics = UrlManagerTopic.URLS,
        containerFactory = "urlManagerKafkaListenerContainerFactory"
)
public class UrlManagerListener {

    private final TargetUrlService targetUrlService;

    public UrlManagerListener(TargetUrlService targetUrlService) {
        this.targetUrlService = targetUrlService;
    }

    @KafkaHandler
    public void on(UrlDownloadedEvent event) {
        log.info("Received Url Downloaded Event: {}", event);
        this.targetUrlService.handleUrlDownloadedEvent(event);
    }

    @KafkaHandler(isDefault = true)
    public void on(Object event) {
        log.info("Default event handler, received event: {}", event);
    }
}
