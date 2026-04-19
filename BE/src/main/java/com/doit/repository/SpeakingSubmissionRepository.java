package com.doit.repository;

import com.doit.entity.SpeakingSubmission;
import com.doit.entity.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeakingSubmissionRepository extends JpaRepository<SpeakingSubmission, Long> {

    List<SpeakingSubmission> findByAttempt(UserAttempt attempt);

    List<SpeakingSubmission> findByAttemptOrderByPartPartNumberAsc(UserAttempt attempt);

    Optional<SpeakingSubmission> findByAttemptAndPartId(UserAttempt attempt, Long partId);
}
