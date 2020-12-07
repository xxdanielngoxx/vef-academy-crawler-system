package io.vef.academy.url.manager.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "seed_urls")
@Access(AccessType.FIELD)
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeedUrl {

    @Id
    private String id;

    private String url;

    @JsonIgnore
    @OneToMany(
            mappedBy = "seedUrl",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @MapKey(name = "id")
    private List<SeedDetails> seedDetailsList;

    private SeedUrl(String id, String url) {
        this.id = id;
        this.url = url;
        this.seedDetailsList = new LinkedList<>();
    }

    public static SeedUrl of(String id, String url) {
        return new SeedUrl(id, url);
    }

    public Optional<SeedDetails> addUrl(SeedDetails newSeedDetails) {
        for (SeedDetails seedDetails: seedDetailsList) {
            if (newSeedDetails.getSeedUrl().equals(this) && seedDetails.equals(newSeedDetails)) {
                return Optional.empty();
            }
        }
        seedDetailsList.add(newSeedDetails);
        return Optional.of(newSeedDetails);
    }

    public Optional<List<SeedDetails>> removeUrl(SeedDetails targetSeedDetails) {

        List<SeedDetails> removedList = new ArrayList<>();

        for (SeedDetails seedDetails: seedDetailsList) {
            if (targetSeedDetails.getSeedUrl().equals(this) && targetSeedDetails.equals(seedDetails)) {
                seedDetailsList.remove(targetSeedDetails);
                removedList.add(seedDetails);
            }
        }
        return removedList.isEmpty() ? Optional.empty() : Optional.of(removedList);
    }

    public List<SeedDetails> getSeedDetailsList() {
        return Collections.unmodifiableList(seedDetailsList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedUrl seedUrl = (SeedUrl) o;

        return id.equals(seedUrl.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}