package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UrlDownloadedEvent implements Serializable {

    private static final long serialVersionUID = 4229424651513731273L;

    private static final String FROM = "URL-MANAGER-SERVICE";

    private String url;

    private String taskId;

    private String downloadedId;

    private Set<String> seedIds;

    private Metadata metadata;

    private UrlDownloadedEvent(String url, String taskId, String downloadedId, Set<String> seedIds) {
        this.url = url;
        this.taskId = taskId;
        this.downloadedId = downloadedId;
        this.seedIds = Collections.unmodifiableSet(seedIds);
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDownloadedEvent of(String url, String taskId, String contentId, Set<String> seedUrlList) {
        return new UrlDownloadedEvent(url, taskId,contentId, seedUrlList);
    }
}
