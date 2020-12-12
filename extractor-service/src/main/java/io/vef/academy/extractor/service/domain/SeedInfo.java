package io.vef.academy.extractor.service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;

@Getter
@NoArgsConstructor
@Document
@CompoundIndex(def = "{'taskId': 1, 'seedId': 1}", unique = true)
public class SeedInfo implements Serializable {

    private static final long serialVersionUID = -928242679618377447L;

    @Id
    private ObjectId id;

    private String taskId;

    private String seedId;

    private Map<String, String> extractedProperties = new HashMap<>();

    private List<String> trustedSites = new LinkedList<>();

    @PersistenceConstructor
    public SeedInfo(String taskId, String seedId, Map<String, String> extractedProperties, List<String> trustedSites) {
        this.taskId = taskId;
        this.seedId = seedId;
        this.extractedProperties = extractedProperties;
        this.trustedSites = trustedSites;
    }

    public Map<String, String> getExtractedProperties() {
        return Collections.unmodifiableMap(extractedProperties);
    }

    public List<String> getTrustedSites() {
        return Collections.unmodifiableList(trustedSites);
    }

    public void setExtractedProperties(Map<String, String> extractedProperties) {
        this.extractedProperties = extractedProperties;
    }

    public void setTrustedSites(List<String> trustedSites) {
        this.trustedSites = trustedSites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedInfo seedInfo = (SeedInfo) o;

        return id.equals(seedInfo.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
