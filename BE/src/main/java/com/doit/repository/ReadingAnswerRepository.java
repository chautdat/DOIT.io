package com.doit.repository;

import com.doit.entity.ReadingAnswer;
import com.doit.entity.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingAnswerRepository extends JpaRepository<ReadingAnswer, Long> {

    List<ReadingAnswer> findByAttempt(UserAttempt attempt);

    List<ReadingAnswer> findByAttemptOrderByQuestionOrderNumber(UserAttempt attempt);

    Long countByAttemptAndIsCorrectTrue(UserAttempt attempt);

    Long countByAttempt(UserAttempt attempt);
}
