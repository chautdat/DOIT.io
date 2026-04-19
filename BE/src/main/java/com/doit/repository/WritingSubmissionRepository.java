package com.doit.repository;

import com.doit.entity.UserAttempt;
import com.doit.entity.WritingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WritingSubmissionRepository extends JpaRepository<WritingSubmission, Long> {

    List<WritingSubmission> findByAttempt(UserAttempt attempt);

    List<WritingSubmission> findByAttemptOrderByTaskTaskNumberAsc(UserAttempt attempt);

    Optional<WritingSubmission> findByAttemptAndTaskId(UserAttempt attempt, Long taskId);
}
