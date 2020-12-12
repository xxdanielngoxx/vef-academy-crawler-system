package io.vef.academy.extractor.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document
@CompoundIndex(def = "{'url': 1, 'taskId': 1}", unique = true)
public class TargetUrl implements Serializable {

    private static final long serialVersionUID = 8800084333428123309L;

    @Id
    private ObjectId id;

    private String url;

    private String taskId;

    @Indexed(unique = true)
    private String downloadedId;

    private String content;

    private RootUrlStatus status;

    private TargetUrl(String url, String taskId, String downloadedId) {
        this.url = url;
        this.taskId = taskId;
        this.downloadedId = downloadedId;
        this.status = RootUrlStatus.INITIALIZED;
    }

    public static TargetUrl of(String url, String taskId, String downloadedId) {
        return new TargetUrl(url, taskId, downloadedId);
    }

    public Optional<TargetUrl> cloneContent(String content) {
        if (this.status == RootUrlStatus.INITIALIZED) {
            this.status = RootUrlStatus.CONTENT_CLONED;
            this.content = content;
            return Optional.of(this);
        }
        log.debug("Previous status must be: {}", RootUrlStatus.INITIALIZED);
        return Optional.empty();
    }

    public enum RootUrlStatus {
        INITIALIZED,
        CONTENT_CLONED,
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetUrl targetUrl = (TargetUrl) o;

        return id.equals(targetUrl.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
