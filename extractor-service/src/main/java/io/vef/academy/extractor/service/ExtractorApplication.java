package io.vef.academy.extractor.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ExtractorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExtractorApplication.class);
    }
}
