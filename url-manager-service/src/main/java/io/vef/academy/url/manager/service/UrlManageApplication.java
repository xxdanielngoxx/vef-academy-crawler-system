package io.vef.academy.url.manager.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class UrlManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(UrlManageApplication.class);
    }
}
