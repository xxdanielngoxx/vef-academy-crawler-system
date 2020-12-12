package io.vef.academy.extractor.service.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;

public class Extracted implements Serializable {

    private static final long serialVersionUID = 8354360705174838744L;

    private String seedId;

    @DBRef
    private SeedInfo seedInfo;

    public Extracted(String seedId) {
        this.seedId = seedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Extracted that = (Extracted) o;

        return seedId != null ? seedId.equals(that.seedId) : that.seedId == null;
    }

    @Override
    public int hashCode() {
        return seedId != null ? seedId.hashCode() : 0;
    }
}
