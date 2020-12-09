package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import seed.SeedUrl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UrlDownloadedEvent implements Serializable {

    private static final long serialVersionUID = 4229424651513731273L;

    private static final String FROM = "URL-MANAGER-SERVICE";

    private String url;

    private String taskId;

    private String contentId;

    private List<SeedUrl> seedUrlList;

    private Metadata metadata;

    private UrlDownloadedEvent(String url, String taskId, String contentId, List<SeedUrl> seedUrlList) {
        this.url = url;
        this.taskId = taskId;
        this.contentId = contentId;
        this.seedUrlList = Collections.unmodifiableList(seedUrlList);
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDownloadedEvent of(String url, String taskId, String contentId, List<SeedUrl> seedUrlList) {
        return new UrlDownloadedEvent(url, taskId,contentId, seedUrlList);
    }
}
