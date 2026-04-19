package com.doit.repository;

import com.doit.entity.User;
import com.doit.entity.UserAttempt;
import com.doit.entity.WritingSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WritingSubmissionRepository extends JpaRepository<WritingSubmission, Long> {

    List<WritingSubmission> findByAttempt(UserAttempt attempt);

    List<WritingSubmission> findByAttemptOrderByTaskTaskNumberAsc(UserAttempt attempt);

    List<WritingSubmission> findByAttemptOrderByTaskTaskNumber(UserAttempt attempt);

    Optional<WritingSubmission> findByAttemptAndTaskId(UserAttempt attempt, Long taskId);

    Page<WritingSubmission> findByAttemptUserOrderBySubmittedAtDesc(User user, Pageable pageable);
}
