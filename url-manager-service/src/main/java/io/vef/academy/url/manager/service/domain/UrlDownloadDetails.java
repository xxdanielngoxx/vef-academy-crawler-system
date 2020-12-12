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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@ToString(exclude = "url")
@Getter
@Entity
@Table(name = "url_download_details")
@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class UrlDownloadDetails implements Serializable {

    private static final long serialVersionUID = 3131714910584713280L;

    private UrlDownloadDetails(Url url, String taskId) {
        this.id = UrlDownloadDetailsId.of(url.getUrl(), taskId);
        this.url = url;
        this.status = ProcessStatus.DISPATCHED;
        this.dispatchedDate = LocalDateTime.now();
    }

    public static UrlDownloadDetails newDetails(Url url, String taskId, String seedId) {
        UrlDownloadDetails newUrlDownloadDetails = new UrlDownloadDetails(url, taskId);
        newUrlDownloadDetails.addUrlExtractDetails(seedId);
        return newUrlDownloadDetails;
    }

    @Id
    private UrlDownloadDetailsId id;

    @JsonIgnore
    @ManyToOne
    @MapsId("url")
    @JoinColumn(name = "url", referencedColumnName = "url")
    private Url url;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @Column(name = "downloaded_id")
    private String downloadedId;

    @JsonIgnore
    @MapKey(name = "id")
    @OneToMany(
            mappedBy = "urlDownloadDetails",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UrlExtractDetails> urlExtractDetailsList = new LinkedList<>();

    @Column(name = "dispatched_date")
    private LocalDateTime dispatchedDate;

    @Column(name = "downloaded_date")
    private LocalDateTime downloadedDate;

    @Column(name = "failed_date")
    private LocalDateTime failedDate;

    @Version
    private int version;

    public Optional<UrlExtractDetails> addUrlExtractDetails(String seedId) {
        if (status == ProcessStatus.DOWNLOAD_FAILED) {
            log.error("Can not add extract details url {} because download failed", id.getUrl());
            throw new IllegalStateException("Can not add extract details url " + id.getUrl() + " because download failed");
        }

        Optional<UrlExtractDetails> optionalUrlExtractDetails = this.findUrlExtractDetailsBySeedId(seedId);

        if (optionalUrlExtractDetails.isPresent()) {
            log.info("Extract Detail: {}, already existed", optionalUrlExtractDetails.get());
            return Optional.empty();
        }

        UrlExtractDetails newUrlExtractDetails = UrlExtractDetails.of(this, seedId);
        urlExtractDetailsList.add(newUrlExtractDetails);
        return Optional.of(newUrlExtractDetails);
    }

    public Optional<UrlDownloadDetails> markAsDownloaded(String downloadedId) {
        if (this.status == ProcessStatus.DISPATCHED || this.status == ProcessStatus.RETRIED) {
            this.status = ProcessStatus.DOWNLOADED;
            this.downloadedId = downloadedId;
            this.downloadedDate = LocalDateTime.now();
            this.markUrlExtractDetailsAsExtracting();
            return Optional.of(this);
        }
        log.error("Previous state must be: {} or {}", ProcessStatus.DISPATCHED, ProcessStatus.RETRIED);
        return Optional.empty();
    }

    public Optional<UrlDownloadDetails> markAsDownloadFailed() {
        if (this.status == ProcessStatus.DISPATCHED || this.status == ProcessStatus.RETRIED) {
            this.status = ProcessStatus.DOWNLOAD_FAILED;
            this.failedDate = LocalDateTime.now();
            this.markUrlExtractDetailsAsFailed();
            return Optional.of(this);
        }
        log.error("Previous state must be: {}  or {}", ProcessStatus.DISPATCHED, ProcessStatus.RETRIED);
        return Optional.empty();
    }

    public Optional<UrlDownloadDetails> markAsRetrying() {
        if (status == ProcessStatus.DISPATCHED) {
            this.status = ProcessStatus.RETRIED;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    public List<UrlExtractDetails> getUrlExtractDetailsList() {
        return Collections.unmodifiableList(urlExtractDetailsList);
    }

    private UrlDownloadDetails markUrlExtractDetailsAsExtracting() {
        for (UrlExtractDetails extractDetails : urlExtractDetailsList) {
            extractDetails.markAsExtracting();
        }
        return this;
    }

    private UrlDownloadDetails markUrlExtractDetailsAsFailed() {
        for (UrlExtractDetails extractDetails : urlExtractDetailsList) {
            extractDetails.markAsFailed();
        }
        return this;
    }

    private Optional<UrlExtractDetails> findUrlExtractDetailsBySeedId(String seedId) {
        return urlExtractDetailsList
                .stream()
                .filter(urlExtractDetails -> urlExtractDetails.getId().equals(UrlExtractDetailsId.of(id, seedId)))
                .findFirst();
    }

    public enum ProcessStatus {
        DISPATCHED,
        DOWNLOADED,
        RETRIED,
        DOWNLOAD_FAILED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlDownloadDetails that = (UrlDownloadDetails) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
