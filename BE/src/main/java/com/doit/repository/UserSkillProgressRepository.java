package com.doit.repository;

import com.doit.entity.UserSkillProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillProgressRepository extends MongoRepository<UserSkillProgress, String> {

    List<UserSkillProgress> findByUserId(String userId);

    Optional<UserSkillProgress> findByUserIdAndSkill(String userId, String skill);
}
