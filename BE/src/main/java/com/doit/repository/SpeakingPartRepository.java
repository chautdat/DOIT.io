package com.doit.repository;

import com.doit.entity.SpeakingPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakingPartRepository extends JpaRepository<SpeakingPart, Long> {
    
    List<SpeakingPart> findByExamIdOrderByPartNumber(Long examId);
}
