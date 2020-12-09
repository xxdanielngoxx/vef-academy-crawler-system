package io.vef.academy.url.manager.service.integration;

import io.vef.academy.common.events.UrlDownloadSucceedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import topics.UrlManagerTopic;

@Slf4j
@Component
@KafkaListener(
        id = "url-manager-listener",
        topics = UrlManagerTopic.URLS,
        containerFactory = "urlManagerKafkaListenerContainerFactory"
)
public class UrlManagerListener {

    public void on(UrlDownloadSucceedEvent event, Acknowledgment acknowledgment) {

        acknowledgment.acknowledge();
    }

    @KafkaHandler(isDefault = true)
    public void on(Object event, Acknowledgment acknowledgment) {
        log.info("Default event handler, event: {}", event);
        acknowledgment.acknowledge();
    }
}
