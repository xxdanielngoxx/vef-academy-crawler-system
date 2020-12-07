package io.vef.academy.download.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "downloads")
@Access(AccessType.FIELD)
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Download {

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

    @NaturalId
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

    public Download markDownloaded(String content) {
        if (this.status != DownloadStatus.DOWNLOADING) {
            throw new IllegalStateException("Previous status must be " + DownloadStatus.DOWNLOADING);
        }
        this.status = DownloadStatus.DOWNLOADED;
        this.content = content;
        this.downloadedDate = LocalDateTime.now();
        return this;
    }

    public Download markFailed() {
        if (this.status != DownloadStatus.DOWNLOADING) {
            throw new IllegalStateException("Previous status must be " + DownloadStatus.DOWNLOADING);
        }
        this.status = DownloadStatus.FAILED;
        this.failedDate = LocalDateTime.now();
        this.failedTimes += 1;
        return this;
    }

    public enum DownloadStatus {
        DOWNLOADING,
        DOWNLOADED,
        FAILED
    }
}