package io.vef.academy.url.manager.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "urls")
@Access(AccessType.FIELD)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Url implements Serializable {

    private static final long serialVersionUID = 5512731081269678865L;

    private Url(String url, SeedUrl seedUrl) {
        this.url = url;
        this.dispatchedDate = LocalDateTime.now();
        this.urlDetailsList = new LinkedList<>();
        this.seedDetailsList = new LinkedList<>();
    }

    public static Url newUrl(String url, SeedUrl seedUrl) {
        Url newUrl = new Url(url, seedUrl);
        newUrl.addSeedUrl(seedUrl);
        return newUrl;
    }

    @Id
    private String url;

    @OneToMany(
            mappedBy = "url",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SeedDetails> seedDetailsList;

    @Column(name = "dispatched_date")
    private LocalDateTime dispatchedDate;

    @JsonIgnore
    @OneToMany(
            mappedBy = "url",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @MapKey(name = "url")
    private List<UrlDetails> urlDetailsList;

    public Optional<UrlDetails> addUrlDetails(String taskId) {
        if (isExecutedInTheSameTask(taskId)) {
            return Optional.empty();
        }
        UrlDetails newDetails = UrlDetails.newDetails(this, taskId);
        this.urlDetailsList.add(newDetails);
        this.dispatchedDate = LocalDateTime.now();
        return Optional.of(newDetails);
    }

    public String getUrl() {
        return url;
    }

    public List<SeedDetails> getSeedDetails() {
        return Collections.unmodifiableList(this.seedDetailsList);
    }

    public LocalDateTime getDispatchedDate() {
        return dispatchedDate;
    }

    public List<UrlDetails> getUrlDetailsList() {
        return Collections.unmodifiableList(this.urlDetailsList);
    }

    private boolean isExecutedInTheSameTask(String taskId) {
        return this.urlDetailsList.stream().anyMatch(urlDetails -> urlDetails.getTaskId().equals(taskId));
    }

    public Optional<SeedDetails> addSeedUrl(SeedUrl seedUrl) {
        for (SeedDetails seedDetails: seedDetailsList) {
            if (seedDetails.getUrl().equals(this) && seedDetails.getSeedUrl().equals(seedUrl)) {
                return Optional.empty();
            }
        }
        SeedDetails newSeedDetails = SeedDetails.of(this, seedUrl);
        this.seedDetailsList.add(newSeedDetails);
        seedUrl.addUrl(newSeedDetails);
        return Optional.of(newSeedDetails);
    }

    public Optional<List<SeedDetails>> removeSeedUrl(SeedUrl seedUrl) {

        List<SeedDetails> removedList = new ArrayList<>();

        for (SeedDetails seedDetails: seedDetailsList) {
            if (seedDetails.getUrl().equals(this) && seedDetails.getSeedUrl().equals(seedUrl)) {
                this.seedDetailsList.remove(seedDetails);
                seedUrl.removeUrl(seedDetails);
                seedDetails.clear();
                removedList.add(seedDetails);
            }
        }
        return removedList.isEmpty() ? Optional.empty() : Optional.of(removedList);
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
