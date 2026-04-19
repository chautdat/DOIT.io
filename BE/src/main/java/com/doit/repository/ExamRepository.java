package com.doit.repository;

import com.doit.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    Page<Exam> findBySkillAndIsActiveTrue(Exam.Skill skill, Pageable pageable);
    
    Page<Exam> findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill skill, Exam.BandLevel bandLevel, Pageable pageable);
    
    Page<Exam> findByTypeAndIsActiveTrue(Exam.ExamType type, Pageable pageable);
    
    List<Exam> findByTypeAndSkillAndIsActiveTrue(Exam.ExamType type, Exam.Skill skill);
    
    @Query("SELECT e FROM Exam e WHERE e.type = :type AND e.isActive = true ORDER BY FUNCTION('RANDOM')")
    List<Exam> findRandomByType(@Param("type") Exam.ExamType type, Pageable pageable);
    
    long countBySkillAndIsActiveTrue(Exam.Skill skill);
    
    long countByTypeAndIsActiveTrue(Exam.ExamType type);
}
