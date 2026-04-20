package com.doit.repository;

import com.doit.entity.SpeakingSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeakingSubmissionRepository extends MongoRepository<SpeakingSubmission, String> {

    List<SpeakingSubmission> findByAttemptId(String attemptId);

    Optional<SpeakingSubmission> findByAttemptIdAndPartId(String attemptId, String partId);
}
