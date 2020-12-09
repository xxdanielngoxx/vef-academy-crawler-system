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
public class UrlDownloadSucceedEvent implements Serializable {

    private static final long serialVersionUID = 4214511906973927228L;

    private static final String FROM = "DOWNLOAD-SERVICE";

    private String id;

    private String url;

    private String taskId;

    private Metadata metadata;

    private UrlDownloadSucceedEvent(String id, String url, String taskId) {
        this.id = id;
        this.url = url;
        this.taskId = taskId;
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDownloadSucceedEvent of(String id, String url, String taskId) {
        return new UrlDownloadSucceedEvent(id, url, taskId);
    }
}
