package com.doit.repository;

import com.doit.entity.ListeningAnswer;
import com.doit.entity.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningAnswerRepository extends JpaRepository<ListeningAnswer, Long> {

    List<ListeningAnswer> findByAttempt(UserAttempt attempt);

    List<ListeningAnswer> findByAttemptOrderByQuestionOrderNumber(UserAttempt attempt);

    Long countByAttemptAndIsCorrectTrue(UserAttempt attempt);

    Long countByAttempt(UserAttempt attempt);
}
