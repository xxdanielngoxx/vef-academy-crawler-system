package io.vef.academy.starter.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class StarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarterApplication.class);
    }
}
