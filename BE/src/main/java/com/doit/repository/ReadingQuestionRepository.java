package com.doit.repository;

import com.doit.entity.ReadingQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingQuestionRepository extends MongoRepository<ReadingQuestion, String> {
    
    List<ReadingQuestion> findByPassageIdOrderByOrderNumber(String passageId);
    
    long countByPassageId(String passageId);
}
