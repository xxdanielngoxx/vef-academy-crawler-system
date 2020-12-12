package io.vef.academy.starter.service.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import topics.DownloadServiceTopic;
import topics.TrackingTopic;
import topics.UrlManagerTopic;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${io.vef.academy.bootstrap.servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        return new KafkaAdmin(props);
    }

    @Bean
    public NewTopic urlManageTopic() {
        return TopicBuilder.name(UrlManagerTopic.URLS)
                            .partitions(3)
                            .compact()
                            .build();
    }

    @Bean
    public NewTopic trackingTopic() {
        return TopicBuilder.name(TrackingTopic.TRACKING)
                            .partitions(3)
                            .compact()
                            .build();
    }

    @Bean
    public NewTopic downloadTopic() {
        return TopicBuilder.name(DownloadServiceTopic.DOWNLOADS)
                .partitions(3)
                .compact()
                .build();
    }
}
