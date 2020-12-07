package io.vef.academy.url.manager.service.repositories;

import io.vef.academy.url.manager.service.domain.SeedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeedUrlRepository extends JpaRepository<SeedUrl, String> {
}
