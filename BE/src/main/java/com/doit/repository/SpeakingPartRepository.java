package com.doit.repository;

import com.doit.entity.SpeakingPart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakingPartRepository extends MongoRepository<SpeakingPart, String> {
    
    List<SpeakingPart> findByExamId(String examId);
    
    List<SpeakingPart> findByExamIdOrderByPartType(String examId);
}
