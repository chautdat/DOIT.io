package com.doit.repository;

import com.doit.entity.WritingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WritingTaskRepository extends JpaRepository<WritingTask, Long> {
    
    List<WritingTask> findByExamIdOrderByTaskNumber(Long examId);
}
