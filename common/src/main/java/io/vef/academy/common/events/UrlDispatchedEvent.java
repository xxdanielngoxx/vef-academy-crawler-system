package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import seed.SeedUrl;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UrlDispatchedEvent {

    private static final String FROM = "URL-MANAGER-SERVICE";

    private String detailId;

    private String url;

    private SeedUrl seedUrl;

    private String taskId;

    private Metadata metadata;

    private UrlDispatchedEvent(String detailId, String url, SeedUrl seedUrl, String taskId) {
        this.detailId = detailId;
        this.url = url;
        this.seedUrl = seedUrl;
        this.taskId = taskId;
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDispatchedEvent of(String detailId, String url, SeedUrl seedUrl, String taskId) {
        return new UrlDispatchedEvent(detailId, url, seedUrl, taskId);
    }
}
