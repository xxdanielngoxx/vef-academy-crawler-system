package io.vef.academy.url.manager.service.integration;

import io.vef.academy.common.events.TaskExecutedEvent;
import io.vef.academy.url.manager.service.services.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import topics.TrackingTopic;

@Slf4j
@Component
@KafkaListener(id = "url-manager-tracking-listener", topics = TrackingTopic.TRACKING, containerFactory = "trackingKafkaListenerContainerFactory")
public class TrackingListener {

    private final UrlService urlService;

    public TrackingListener(UrlService urlService) {
        this.urlService = urlService;
    }

    @KafkaHandler
    public void on(TaskExecutedEvent taskExecutedEvent, Acknowledgment acknowledgment) {
        log.info("TaskExecuted event: {}", taskExecutedEvent.toString());
        this.urlService.handleTaskExecuted(taskExecutedEvent);
        acknowledgment.acknowledge();
    }

    @KafkaHandler(isDefault = true)
    public void on(Object message, Acknowledgment acknowledgment) {
        log.info("Default message handler, message: {}", message.toString());
        acknowledgment.acknowledge();
    }
}
