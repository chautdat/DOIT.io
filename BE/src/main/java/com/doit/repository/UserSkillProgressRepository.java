package com.doit.repository;

import com.doit.entity.Exam;
import com.doit.entity.User;
import com.doit.entity.UserSkillProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillProgressRepository extends JpaRepository<UserSkillProgress, Long> {

    List<UserSkillProgress> findByUser(User user);

    Optional<UserSkillProgress> findByUserAndSkill(User user, Exam.Skill skill);
}
