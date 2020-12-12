package io.vef.academy.extractor.service.services;

import io.vef.academy.common.events.UrlDownloadedEvent;
import io.vef.academy.extractor.service.domain.TargetUrl;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.stereotype.Service;

@Service
public interface TargetUrlService {
    public void handleUrlDownloadedEvent(UrlDownloadedEvent event);
}
