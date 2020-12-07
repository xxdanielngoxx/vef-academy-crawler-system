package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContentDownloadFailedEvent {

    private static final String FROM = "DOWNLOAD-SERVICE";

    private String id;

    private String url;

    private String taskId;

    private int failedTimes;

    private Metadata metadata;

    private ContentDownloadFailedEvent(String id, String url, String taskId, int failedTimes) {
        this.id = id;
        this.url = url;
        this.taskId = taskId;
        this.failedTimes = failedTimes;
        this.metadata = Metadata.of(FROM);
    }

    public static ContentDownloadFailedEvent of(String id, String url, String taskId, int failedTimes) {
        return new ContentDownloadFailedEvent(id, url, taskId, failedTimes);
    }
}
