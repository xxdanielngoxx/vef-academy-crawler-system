package io.vef.academy.url.manager.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
@Getter
@ToString(exclude = "urlDownloadDetails")
@Entity
@Table(name = "url_extract_details")
@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class UrlExtractDetails implements Serializable {

    private static final long serialVersionUID = 4099906407363519507L;

    @EmbeddedId
    private UrlExtractDetailsId id;

    @Column(name = "extracted_id")
    private String extractedId;

    @Enumerated(EnumType.STRING)
    private ExtractStatus status;

    @Column(name = "extracted_date")
    private LocalDateTime extractedDate;

    @Column(name = "failed_date")
    private LocalDateTime failedDate;

    @JsonIgnore
    @MapsId("urlDownloadDetailsId")
    @JoinColumns({
            @JoinColumn(name = "url", referencedColumnName = "url"),
            @JoinColumn(name = "task_id", referencedColumnName = "task_id")
    })
    @ManyToOne
    private UrlDownloadDetails urlDownloadDetails;

    @Version
    private int version;

    private UrlExtractDetails(UrlDownloadDetails urlDownloadDetails, String seedId) {
        this.id = UrlExtractDetailsId.of(
                urlDownloadDetails.getId().getUrl(),
                urlDownloadDetails.getId().getTaskId(),
                seedId
        );
        this.urlDownloadDetails = urlDownloadDetails;
        this.status = ExtractStatus.WAITING_DOWNLOAD;
    }

    public static UrlExtractDetails of(UrlDownloadDetails urlDownloadDetails, String seedId) {
        return new UrlExtractDetails(urlDownloadDetails, seedId);
    }

    public UrlExtractDetails markAsExtracting() {
        if (status != ExtractStatus.WAITING_DOWNLOAD) {
            log.error("Previous state must be: {}", ExtractStatus.WAITING_DOWNLOAD);
            throw new IllegalStateException("Previous state must be: " + ExtractStatus.WAITING_DOWNLOAD);
        }
        this.status = ExtractStatus.EXTRACTING;
        return this;
    }

    public UrlExtractDetails markAsFailed() {
        if (status != ExtractStatus.WAITING_DOWNLOAD) {
            log.error("Previous state must be: {}", ExtractStatus.WAITING_DOWNLOAD);
            throw new IllegalStateException("Previous state must be: " + ExtractStatus.WAITING_DOWNLOAD);
        }
        this.status = ExtractStatus.FAILED;
        this.failedDate = LocalDateTime.now();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlExtractDetails that = (UrlExtractDetails) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public enum ExtractStatus {
        WAITING_DOWNLOAD,
        EXTRACTING,
        EXTRACTED,
        FAILED
    }
}
