package seed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeedUrl implements Serializable {

    private static final long serialVersionUID = -8521588534082015222L;

    private String id;
    private String url;
}
