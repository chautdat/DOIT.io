package com.doit.repository;

import com.doit.entity.ListeningQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningQuestionRepository extends MongoRepository<ListeningQuestion, String> {
    
    List<ListeningQuestion> findByAudioIdOrderByOrderNumber(String audioId);
    
    long countByAudioId(String audioId);
}
