package io.vef.academy.url.manager.service.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import topics.UrlManagerTopic;

@Slf4j
@Component
@KafkaListener(id = "url-manager-listener", topics = UrlManagerTopic.URLS, containerFactory = "urlManagerKafkaListenerContainerFactory")
public class UrlManagerListener {
    @KafkaHandler(isDefault = true)
    public void on(Object message, Acknowledgment acknowledgment) {
        log.info("Default message handler, message: {}", message.toString());
        acknowledgment.acknowledge();
    }
}
