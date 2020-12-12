package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UrlDownloadRetriedEvent {
    private static final String FROM = "URL-MANAGER-SERVICE";
    private String url;
    private String taskId;
    private String downloadId;
    private Metadata metadata;

    private UrlDownloadRetriedEvent(String url, String taskId, String downloadId) {
        this.url = url;
        this.taskId = taskId;
        this.downloadId = downloadId;
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDownloadRetriedEvent of(String url, String taskId, String downloadId) {
        return new UrlDownloadRetriedEvent(url, taskId, downloadId);
    }
}
