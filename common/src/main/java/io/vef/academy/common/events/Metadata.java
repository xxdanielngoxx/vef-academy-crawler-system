package io.vef.academy.common.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Metadata implements Serializable {

    private static final long serialVersionUID = 5618774268773369190L;

    private String from;

    private LocalDateTime createdDate;

    private Metadata(String from) {
        this.from = from.toUpperCase();
        this.createdDate = LocalDateTime.now();
    }

    public static Metadata of(String from) {
        return new Metadata(from);
    }
}
