package seed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SeedUrl implements Serializable {

    private static final long serialVersionUID = -8521588534082015222L;

    @Column(name = "seed_id")
    private String id;

    @Column(name = "seed_url")
    private String url;
}
