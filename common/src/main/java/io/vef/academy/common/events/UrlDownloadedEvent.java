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
public class UrlDownloadedEvent {

    private static final String FROM = "URL-MANAGER-SERVICE";

    private String url;

    private Metadata metadata;

    private UrlDownloadedEvent(String url) {
        this.url = url;
        this.metadata = Metadata.of(FROM);
    }

    public static UrlDownloadedEvent of(String url) {
        return new UrlDownloadedEvent(url);
    }
}
