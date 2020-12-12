package io.vef.academy.url.manager.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Entity
@Table(name = "urls")
@Access(AccessType.FIELD)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Url implements Serializable {

    private static final long serialVersionUID = 5512731081269678865L;

    private Url(String url) {
        this.url = url;
        this.dispatchedDate = LocalDateTime.now();
    }

    public static Url newUrl(String url, String taskId, String seedId) {
        Url newUrl = new Url(url);
        newUrl.addUrlDownloadDetails(taskId, seedId);
        return newUrl;
    }

    @Id
    private String url;

    @Column(name = "dispatched_date")
    private LocalDateTime dispatchedDate;

    @OneToMany(
            mappedBy = "url",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @MapKey(name = "url")
    private List<UrlDownloadDetails> urlDownloadDetailsList = new LinkedList<>();

    @Version
    private int version;

    public Optional<UrlDownloadDetails> addUrlDownloadDetails(String taskId, String seedId) {
        Optional<UrlDownloadDetails> targetDownloadDetails = this.findUrlDownloadDetailsByTaskId(taskId);

        if (targetDownloadDetails.isPresent()) {
            return this.addExtractDetailsInExistedDownloadDetails(targetDownloadDetails.get(), seedId);
        }

        return Optional.of(this.addNewDownloadDetails(taskId, seedId));
    }

    public Optional<UrlDownloadDetails> markUrlDownloadDetailsAsDownloaded(String taskId, String downloadId) {
        UrlDownloadDetails targetDownloadDetails = this.getUrlDownloadDetailsByTaskId(taskId);
        return targetDownloadDetails.markAsDownloaded(downloadId);
    }

    public Optional<UrlDownloadDetails> markUrlDownloadDetailsAsRetrying(String taskId) {
        UrlDownloadDetails targetDownloadDetails = this.getUrlDownloadDetailsByTaskId(taskId);
        targetDownloadDetails.markAsRetrying();
        return Optional.of(targetDownloadDetails);
    }

    public Optional<UrlDownloadDetails> markUrlDownloadDetailsAsFailed(String taskId) {
        UrlDownloadDetails targetDownloadDetails = this.getUrlDownloadDetailsByTaskId(taskId);
        return targetDownloadDetails.markAsDownloadFailed();
    }

    public Optional<UrlDownloadDetails> findUrlDownloadDetailsByTaskId(String taskId) {
        return urlDownloadDetailsList
                .stream()
                .filter(urlDownloadDetails -> urlDownloadDetails.getId().equals(UrlDownloadDetailsId.of(url, taskId)))
                .findFirst();
    }

    private UrlDownloadDetails getUrlDownloadDetailsByTaskId(String taskId) {
        return this.findUrlDownloadDetailsByTaskId(taskId)
                .orElseThrow(() -> {
                    log.error("{url: {}, taskId: {}} not found", url, taskId);
                    return new RuntimeException("{url: " + url + ", taskId: " + taskId + "} not found");
                });
    }

    private Optional<UrlDownloadDetails> addExtractDetailsInExistedDownloadDetails(
            UrlDownloadDetails existedDownloadDetails,
            String seedId
    ) {
        Optional<UrlExtractDetails> optionalNewExtractDetails = existedDownloadDetails.addUrlExtractDetails(seedId);
        if (optionalNewExtractDetails.isPresent()) {
            log.info(
                    "Added new Extract Details: {} in existed Download Details: {}",
                    optionalNewExtractDetails.get(),
                    existedDownloadDetails
            );
            return Optional.of(existedDownloadDetails);
        }
        log.info("Extract Details of Download Details: {}, already existed", existedDownloadDetails);
        return Optional.empty();
    }

    private UrlDownloadDetails addNewDownloadDetails(String taskId, String seedId) {
        UrlDownloadDetails newDownloadDetails = UrlDownloadDetails.newDetails(this, taskId, seedId);
        urlDownloadDetailsList.add(newDownloadDetails);
        log.info("Added new Download Details: {}", newDownloadDetails);
        return newDownloadDetails;
    }

    public Set<String> getAllSeedId(String taskId) {
        return this.getUrlDownloadDetailsByTaskId(taskId)
                .getUrlExtractDetailsList()
                .stream()
                .map(urlExtractDetails -> urlExtractDetails.getId().getSeedId())
                .collect(Collectors.toSet());
    }

    public List<UrlDownloadDetails> getUrlDownloadDetailsList() {
        return Collections.unmodifiableList(this.urlDownloadDetailsList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Url url1 = (Url) o;

        return url.equals(url1.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
