package io.vef.academy.url.manager.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UrlDownloadDetailsId implements Serializable {

    private static final long serialVersionUID = 6049052613393537564L;

    private String url;

    @Column(name = "task_id")
    private String taskId;

    public static UrlDownloadDetailsId of(String url, String taskId) {
        return new UrlDownloadDetailsId(url, taskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlDownloadDetailsId that = (UrlDownloadDetailsId) o;

        if (!url.equals(that.url)) return false;
        return taskId.equals(that.taskId);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + taskId.hashCode();
        return result;
    }
}
