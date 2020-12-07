package io.vef.academy.url.manager.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString(exclude = "url")
@Getter
@Entity
@Table(name = "url_details")
@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class UrlDetails implements Serializable {

    private static final long serialVersionUID = 3131714910584713280L;

    private UrlDetails(Url url, String taskId) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.taskId = taskId;
        this.status = ProcessStatus.DISPATCHED;
        this.dispatchedDate = LocalDateTime.now();
    }

    public static UrlDetails newDetails(Url url, String taskId) {
        return new UrlDetails(url, taskId);
    }

    @Id
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "url")
    private Url url;

    @Column(name = "task_id")
    private String taskId;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @Column(name = "content_id")
    private String contendId;

    @Column(name = "dispatched_date")
    private LocalDateTime dispatchedDate;

    @Column(name = "downloaded_date")
    private LocalDateTime downloadedDate;

    @Column(name = "extracted_date")
    private LocalDateTime extractedDate;

    @Column(name = "failed_date")
    private LocalDateTime failedDate;

    public enum ProcessStatus {
        DISPATCHED,
        DOWNLOADED,
        EXTRACTED,
        FAILED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlDetails that = (UrlDetails) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
