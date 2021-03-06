package io.vef.academy.download.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Entity
@Table(name = "downloads")
@Access(AccessType.FIELD)
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Download implements Serializable {

    private static final long serialVersionUID = -325451201291200751L;

    public static Download newDownload(String url, String taskId) {
        return new Download(url, taskId);
    }

    private Download(String url, String taskId) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.taskId = taskId;
        this.status = DownloadStatus.DOWNLOADING;
        this.downloadingDate = LocalDateTime.now();
    }

    @Id
    private String id;

    private String url;

    @Column(name = "task_id")
    private String taskId;

    private String content;

    @Enumerated(EnumType.STRING)
    private DownloadStatus status;

    @Column(name = "downloading_date")
    private LocalDateTime downloadingDate;

    @Column(name = "downloaded_date")
    private LocalDateTime downloadedDate;

    @Column(name = "failed_date")
    private LocalDateTime failedDate;

    @Version
    private int version;

    @Column(name = "failed_times")
    private int failedTimes;

    public Optional<Download> markDownloaded(String content) {
        if (this.status == DownloadStatus.DOWNLOADING) {
            this.status = DownloadStatus.DOWNLOADED;
            this.content = content;
            this.downloadedDate = LocalDateTime.now();
            return Optional.of(this);
        }
        log.error("Previous status must be: {}", DownloadStatus.DOWNLOADING);
        return Optional.empty();
    }

    public Optional<Download> markFailed() {
        if (this.status == DownloadStatus.DOWNLOADING) {
            this.status = DownloadStatus.FAILED;
            this.failedDate = LocalDateTime.now();
            this.failedTimes += 1;
            return Optional.of(this);
        }
        log.error("Previous status must be: {}", DownloadStatus.DOWNLOADING);
        return Optional.empty();
    }

    public Optional<Download> retry() {
        if (this.status == DownloadStatus.FAILED) {
            this.status = DownloadStatus.DOWNLOADING;
            return Optional.of(this);
        }
        log.debug("Previous Download State must be: {}", DownloadStatus.FAILED);
        return Optional.empty();
    }

    public enum DownloadStatus {
        DOWNLOADING,
        DOWNLOADED,
        FAILED
    }
}