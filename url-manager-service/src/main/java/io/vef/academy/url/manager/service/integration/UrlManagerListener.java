package io.vef.academy.url.manager.service.integration;

import io.vef.academy.common.events.UrlDownloadSucceedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import topics.UrlManagerTopic;

@Slf4j
@Component
@KafkaListener(
        groupId = "url-manager-listener",
        topics = UrlManagerTopic.URLS,
        containerFactory = "urlManagerKafkaListenerContainerFactory"
)
public class UrlManagerListener {

    @KafkaHandler
    public void on(UrlDownloadSucceedEvent event) {
        log.info("Downloaded Succeed Event: {}", event);
    }

    @KafkaHandler(isDefault = true)
    public void on(Object event) {
        log.info("Default event handler, event: {}", event);
    }
}
