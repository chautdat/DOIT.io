package com.doit.repository;

import com.doit.entity.ListeningAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningAnswerRepository extends MongoRepository<ListeningAnswer, String> {

    List<ListeningAnswer> findByAttemptId(String attemptId);

    Long countByAttemptIdAndIsCorrectTrue(String attemptId);

    Long countByAttemptId(String attemptId);
}
