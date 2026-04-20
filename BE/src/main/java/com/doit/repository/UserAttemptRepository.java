package com.doit.repository;

import com.doit.entity.UserAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAttemptRepository extends MongoRepository<UserAttempt, String> {

    List<UserAttempt> findByUserIdOrderByStartedAtDesc(String userId);

    Page<UserAttempt> findByUserId(String userId, Pageable pageable);

    List<UserAttempt> findByUserIdAndSkillOrderByStartedAtDesc(String userId, String skill);

    Page<UserAttempt> findByUserIdAndSkill(String userId, String skill, Pageable pageable);

    Optional<UserAttempt> findByIdAndUserId(String id, String userId);

    List<UserAttempt> findByUserIdAndStatus(String userId, UserAttempt.AttemptStatus status);

    Optional<UserAttempt> findTopByUserIdAndExamIdAndStatusOrderByCreatedAtDesc(
            String userId, String examId, UserAttempt.AttemptStatus status);

    @Query("{ 'userId': ?0, 'skill': ?1, 'status': 'GRADED' }")
    List<UserAttempt> findGradedAttemptsByUserIdAndSkill(String userId, String skill);

    Long countByUserIdAndSkill(String userId, String skill);

    List<UserAttempt> findByMockTestId(String mockTestId);
}
