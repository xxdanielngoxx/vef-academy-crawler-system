package io.vef.academy.url.manager.service.repositories;

import io.vef.academy.url.manager.service.domain.SeedDetails;
import io.vef.academy.url.manager.service.domain.SeedDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeedDetailsRepository extends JpaRepository<SeedDetails, SeedDetailsId> {
}
