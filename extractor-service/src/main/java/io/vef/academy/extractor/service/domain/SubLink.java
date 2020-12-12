package io.vef.academy.extractor.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubLink implements Serializable {

    private static final long serialVersionUID = -7897795329759746418L;

    private String id;

    private String link;

    private List<String> seedIds = new LinkedList<>();

    private SubLinkStatus status;

    public SubLink(String link) {
        this.id = UUID.randomUUID().toString();
        this.link = link;
        this.status = SubLinkStatus.INITIALIZED;
    }

    public String getLink() {
        return link;
    }

    public List<String> getSeedIds() {
        return Collections.unmodifiableList(seedIds);
    }

    public Optional<SubLink> setSeedIds(List<String> seedIds) {
        if (this.status == SubLinkStatus.INITIALIZED) {
            this.seedIds = seedIds;
            this.status = SubLinkStatus.FETCHED_SEEDS_FROM_ROOT_URL;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    public enum SubLinkStatus {
        INITIALIZED,
        FETCHED_SEEDS_FROM_ROOT_URL
    }
}
