package io.vef.academy.url.manager.service.controllers;

import io.vef.academy.common.events.TaskExecutedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seed.SeedUrl;
import topics.TrackingTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/test")
@RestController
public class TestController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TestController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping
    public void test() {
        List<SeedUrl> urls = new ArrayList();
        urls.add(new SeedUrl(UUID.randomUUID().toString(), "https://batdongsan.com.vn/"));
        urls.add(new SeedUrl(UUID.randomUUID().toString(), "https://homedy.com/"));
        urls.add(new SeedUrl(UUID.randomUUID().toString(), "https://meeyland.com/"));
        String taskId = UUID.randomUUID().toString();
        this.kafkaTemplate.send(
                TrackingTopic.TRACKING,
                taskId,
                TaskExecutedEvent.of(taskId, urls)
        );
    }
}
