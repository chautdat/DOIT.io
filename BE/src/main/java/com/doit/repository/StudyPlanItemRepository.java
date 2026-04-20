package com.doit.repository;

import com.doit.entity.StudyPlanItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudyPlanItemRepository extends MongoRepository<StudyPlanItem, String> {

    List<StudyPlanItem> findByStudyPlanId(String studyPlanId);

    List<StudyPlanItem> findByStudyPlanIdOrderByOrderIndex(String studyPlanId);

    List<StudyPlanItem> findByStudyPlanIdAndSkill(String studyPlanId, String skill);

    List<StudyPlanItem> findByStudyPlanIdAndIsCompletedFalseOrderByScheduledDate(String studyPlanId);

    List<StudyPlanItem> findByStudyPlanIdAndScheduledDate(String studyPlanId, LocalDate date);

    List<StudyPlanItem> findByStudyPlanIdAndScheduledDateBetween(String studyPlanId, 
                                                                  LocalDate startDate, 
                                                                  LocalDate endDate);

    Long countByStudyPlanId(String studyPlanId);

    Long countByStudyPlanIdAndIsCompletedTrue(String studyPlanId);
}
