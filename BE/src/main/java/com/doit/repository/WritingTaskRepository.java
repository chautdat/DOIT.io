package com.doit.repository;

import com.doit.entity.WritingTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WritingTaskRepository extends MongoRepository<WritingTask, String> {
    
    List<WritingTask> findByExamId(String examId);
    
    List<WritingTask> findByExamIdOrderByTaskType(String examId);
}
