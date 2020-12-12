package io.vef.academy.extractor.service.repositories;

import io.vef.academy.extractor.service.domain.TargetUrl;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TargetUrlRepository extends MongoRepository<TargetUrl, ObjectId> {
    public Optional<TargetUrl> findByUrlAndTaskId(String url, String taskId);
}
