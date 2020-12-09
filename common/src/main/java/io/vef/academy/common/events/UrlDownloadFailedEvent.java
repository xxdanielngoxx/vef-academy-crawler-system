package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UrlDownloadFailedEvent implements Serializable {

    private static final long serialVersionUID = 1056206019047538499L;

    private static final String FROM = "DOWNLOAD-SERVICE";

    private String id;

    private String url;

    private String taskId;

    private int failedTimes;

    private Metadata metadata;

    private UrlDownloadFailedEvent(String id, String url, String taskId, int failedTimes) {
        this.id = id;
        this.url = url;
        this.taskId = taskId;
        this.failedTimes = failedTimes;
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDownloadFailedEvent of(String id, String url, String taskId, int failedTimes) {
        return new UrlDownloadFailedEvent(id, url, taskId, failedTimes);
    }
}
