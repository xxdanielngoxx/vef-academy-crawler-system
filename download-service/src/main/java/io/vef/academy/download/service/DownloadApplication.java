package io.vef.academy.download.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DownloadApplication {
    public static void main(String[] args) {
        SpringApplication.run(DownloadApplication.class);
    }
}
