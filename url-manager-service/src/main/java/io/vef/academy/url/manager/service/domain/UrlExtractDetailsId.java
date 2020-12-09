package io.vef.academy.url.manager.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlExtractDetailsId implements Serializable {

    private static final long serialVersionUID = -2086193773818212989L;

    private UrlDownloadDetailsId urlDownloadDetailsId;

    @Column(name = "seed_id")
    private String seedId;

    private UrlExtractDetailsId(String url, String taskId, String seedId) {
        this.urlDownloadDetailsId = UrlDownloadDetailsId.of(url, taskId);
        this.seedId = seedId;
    }

    public static UrlExtractDetailsId of(String url, String taskId, String seedId) {
        return new UrlExtractDetailsId(url, taskId, seedId);
    }

    public static UrlExtractDetailsId of(UrlDownloadDetailsId urlDownloadDetailsId,String seedId) {
        return new UrlExtractDetailsId(urlDownloadDetailsId, seedId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlExtractDetailsId that = (UrlExtractDetailsId) o;

        if (!urlDownloadDetailsId.equals(that.urlDownloadDetailsId)) return false;
        return seedId.equals(that.seedId);
    }

    @Override
    public int hashCode() {
        int result = urlDownloadDetailsId.hashCode();
        result = 31 * result + seedId.hashCode();
        return result;
    }
}
