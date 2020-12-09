package io.vef.academy.url.manager.service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private static final String TRUSTED_PACKAGES_DESERIALIZER = "io.vef.academy.common.events";

    @Value("${io.vef.academy.bootstrap.servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES_DESERIALIZER);

        return props;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs()
        );
    }

    @Bean(name = "trackingKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> trackingKafkaListenerContainerFactory() {
        return this.generateKafkaListenerContainerFactory();
    }

    @Bean(name = "urlManagerKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> urlManagerKafkaListenerContainerFactory() {
        return this.generateKafkaListenerContainerFactory();
    }

    @Bean(name = "downloadsKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> downloadsKafkaListenerContainerFactory() {
        return this.generateKafkaListenerContainerFactory();
    }

    private ConcurrentKafkaListenerContainerFactory<String, Object> generateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory());
        containerFactory.setConcurrency(3);
        containerFactory.getContainerProperties().setPollTimeout(3000);
        containerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return containerFactory;
    }
}
