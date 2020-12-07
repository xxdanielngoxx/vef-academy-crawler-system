package io.vef.academy.url.manager.service.services;

import io.vef.academy.common.events.TaskExecutedEvent;
import io.vef.academy.common.events.UrlDispatchedEvent;
import io.vef.academy.url.manager.service.domain.Url;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    Url handleUrlDispatched(UrlDispatchedEvent urlDispatchedEvent);
    void handleTaskExecuted(TaskExecutedEvent taskExecutedEvent);
}
