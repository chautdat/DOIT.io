package com.doit.repository;

import com.doit.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends MongoRepository<Exam, String> {
    
    Page<Exam> findBySkillAndIsActiveTrue(Exam.Skill skill, Pageable pageable);
    
    Page<Exam> findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill skill, Exam.BandLevel bandLevel, Pageable pageable);
    
    Page<Exam> findByTypeAndIsActiveTrue(Exam.ExamType type, Pageable pageable);
    
    List<Exam> findByTypeAndSkillAndIsActiveTrue(Exam.ExamType type, Exam.Skill skill);
    
    @Aggregation(pipeline = {
        "{ $match: { type: ?0, isActive: true } }",
        "{ $sample: { size: ?1 } }"
    })
    List<Exam> findRandomByType(Exam.ExamType type, int size);
    
    long countBySkillAndIsActiveTrue(Exam.Skill skill);
    
    long countByTypeAndIsActiveTrue(Exam.ExamType type);

    List<Exam> findBySkillAndIsActiveTrue(Exam.Skill skill);
    
    List<Exam> findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill skill, Exam.BandLevel bandLevel);
    
    List<Exam> findBySkillAndTypeAndIsActiveTrue(Exam.Skill skill, Exam.ExamType type);
    
    List<Exam> findBySkillAndBandLevelAndTypeAndIsActiveTrue(Exam.Skill skill, Exam.BandLevel bandLevel, Exam.ExamType type);
}
