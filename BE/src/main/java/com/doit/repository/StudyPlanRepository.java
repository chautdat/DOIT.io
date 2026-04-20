package com.doit.repository;

import com.doit.entity.StudyPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanRepository extends MongoRepository<StudyPlan, String> {

    List<StudyPlan> findByUserId(String userId);

    Optional<StudyPlan> findByIdAndUserId(String id, String userId);

    Optional<StudyPlan> findByUserIdAndIsActiveTrue(String userId);

    List<StudyPlan> findByUserIdOrderByCreatedAtDesc(String userId);
}
