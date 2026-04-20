package com.doit.repository;

import com.doit.entity.ReadingAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingAnswerRepository extends MongoRepository<ReadingAnswer, String> {

    List<ReadingAnswer> findByAttemptId(String attemptId);

    Long countByAttemptIdAndIsCorrectTrue(String attemptId);

    Long countByAttemptId(String attemptId);
}
