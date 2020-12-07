package io.vef.academy.url.manager.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SeedDetailsId implements Serializable {

    private static final long serialVersionUID = -3233117428813039644L;

    @Column(name = "seed_id")
    private String seedId;

    @Column(name = "url_id")
    private String urlId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedDetailsId that = (SeedDetailsId) o;

        if (!seedId.equals(that.seedId)) return false;
        return urlId.equals(that.urlId);
    }

    @Override
    public int hashCode() {
        int result = seedId.hashCode();
        result = 31 * result + urlId.hashCode();
        return result;
    }
}
