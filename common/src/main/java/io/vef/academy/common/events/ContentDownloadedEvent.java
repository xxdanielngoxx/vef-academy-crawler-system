package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContentDownloadedEvent {

    private static final String FROM = "DOWNLOAD-SERVICE";

    private String id;

    private String url;

    private String taskId;

    private Metadata metadata;

    private ContentDownloadedEvent(String id, String url, String taskId) {
        this.id = id;
        this.url = url;
        this.taskId = taskId;
        this.metadata = Metadata.of(FROM);
    }

    public static ContentDownloadedEvent of(String id, String url, String taskId) {
        return new ContentDownloadedEvent(id, url, taskId);
    }
}
