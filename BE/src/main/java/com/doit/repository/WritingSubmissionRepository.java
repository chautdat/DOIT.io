package com.doit.repository;

import com.doit.entity.WritingSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WritingSubmissionRepository extends MongoRepository<WritingSubmission, String> {

    List<WritingSubmission> findByAttemptId(String attemptId);

    Optional<WritingSubmission> findByAttemptIdAndTaskId(String attemptId, String taskId);

    Page<WritingSubmission> findByUserIdOrderBySubmittedAtDesc(String userId, Pageable pageable);
}
