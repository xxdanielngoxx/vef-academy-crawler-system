package io.vef.academy.url.manager.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "seed_details")
@Access(AccessType.FIELD)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeedDetails implements Serializable {

    private static final long serialVersionUID = 3472131200236555917L;

    @EmbeddedId
    private SeedDetailsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seedId")
    @JoinColumn(name = "seed_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SeedUrl seedUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("urlId")
    @JoinColumn(name = "url_id", referencedColumnName = "url", insertable = false, updatable = false)
    private Url url;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    private SeedDetails(Url url, SeedUrl seedUrl) {
        this.url = url;
        this.seedUrl = seedUrl;
        this.id = new SeedDetailsId(url.getUrl(), seedUrl.getId());
        this.createdDate = LocalDateTime.now();
    }

    public static SeedDetails of(Url url, SeedUrl seedUrl) {
        return new SeedDetails(url, seedUrl);
    }

    public SeedDetails clear() {
        this.url = null;
        this.seedUrl = null;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedDetails that = (SeedDetails) o;

        if (!seedUrl.equals(that.seedUrl)) return false;
        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        int result = seedUrl.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
}
