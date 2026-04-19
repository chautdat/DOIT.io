package com.doit.repository;

import com.doit.entity.Exam;
import com.doit.entity.StudyPlan;
import com.doit.entity.StudyPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {

    List<StudyPlanItem> findByPlan(StudyPlan plan);

    List<StudyPlanItem> findByPlanOrderByOrderNumber(StudyPlan plan);

    List<StudyPlanItem> findByPlanAndSkill(StudyPlan plan, Exam.Skill skill);

    List<StudyPlanItem> findByPlanAndIsCompletedFalseOrderByRecommendedDate(StudyPlan plan);

    List<StudyPlanItem> findByPlanAndRecommendedDate(StudyPlan plan, LocalDate date);

    List<StudyPlanItem> findByPlanAndRecommendedDateBetween(StudyPlan plan, 
                                                            LocalDate startDate, 
                                                            LocalDate endDate);

    Long countByPlan(StudyPlan plan);

    Long countByPlanAndIsCompletedTrue(StudyPlan plan);
}
