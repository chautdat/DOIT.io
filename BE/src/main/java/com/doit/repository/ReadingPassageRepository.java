package com.doit.repository;

import com.doit.entity.ReadingPassage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingPassageRepository extends MongoRepository<ReadingPassage, String> {
    
    List<ReadingPassage> findByExamIdOrderByPassageNumber(String examId);
}
