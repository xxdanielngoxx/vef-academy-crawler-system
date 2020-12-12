package io.vef.academy.extractor.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedValue implements Serializable {

    private static final long serialVersionUID = -423296822126029342L;

    private String key;

    private String value;
}
