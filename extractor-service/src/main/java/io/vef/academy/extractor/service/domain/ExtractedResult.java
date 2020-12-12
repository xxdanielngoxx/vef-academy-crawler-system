package io.vef.academy.extractor.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedResult implements Serializable {

    private static final long serialVersionUID = -3097884601826444824L;


    private List<ExtractedValue> extractedValues = new LinkedList<>();

    private List<SubLink> subLinks = new LinkedList<>();

    List<ExtractedValue> getExtractedValues() {
        return Collections.unmodifiableList(extractedValues);
    }

    public List<SubLink> getSubLinks() {
        return Collections.unmodifiableList(subLinks);
    }
}
