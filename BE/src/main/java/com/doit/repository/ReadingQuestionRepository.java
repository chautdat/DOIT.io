package com.doit.repository;

import com.doit.entity.ReadingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingQuestionRepository extends JpaRepository<ReadingQuestion, Long> {
    
    List<ReadingQuestion> findByPassageIdOrderByOrderNumber(Long passageId);
    
    long countByPassageId(Long passageId);
}
