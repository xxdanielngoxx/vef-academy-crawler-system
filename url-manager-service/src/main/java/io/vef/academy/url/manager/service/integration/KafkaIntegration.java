package io.vef.academy.url.manager.service.integration;

import io.vef.academy.common.events.UrlDispatchedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaIntegration {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaIntegration(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    public void on(UrlDispatchedEvent urlDispatchedEvent) {
        log.info("Url Dispatched event: {}", urlDispatchedEvent);
    }
}
