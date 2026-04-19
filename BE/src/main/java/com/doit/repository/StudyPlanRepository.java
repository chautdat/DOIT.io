package com.doit.repository;

import com.doit.entity.StudyPlan;
import com.doit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    List<StudyPlan> findByUser(User user);

    Optional<StudyPlan> findByIdAndUser(Long id, User user);

    Optional<StudyPlan> findByUserAndIsActiveTrue(User user);

    List<StudyPlan> findByUserOrderByCreatedAtDesc(User user);
}
