package io.vef.academy.download.service.repositories;

import io.vef.academy.download.service.domain.Download;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DownloadRepository extends JpaRepository<Download, String> {
    Optional<Download> findByUrlAndTaskId(String url, String taskId);
}
