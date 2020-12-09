package io.vef.academy.url.manager.service.repositories;

import io.vef.academy.url.manager.service.domain.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByUrl(String url);
}
