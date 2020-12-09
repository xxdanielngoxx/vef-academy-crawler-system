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
public class UrlDispatchedEvent implements Serializable {

    private static final long serialVersionUID = -8807825254312631001L;

    private static final String FROM = "URL-MANAGER-SERVICE";

    private String url;

    private String taskId;

    private Metadata metadata;

    private UrlDispatchedEvent(String url, String taskId) {
        this.url = url;
        this.taskId = taskId;
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDispatchedEvent of(String url, String taskId) {
        return new UrlDispatchedEvent(url, taskId);
    }
}
