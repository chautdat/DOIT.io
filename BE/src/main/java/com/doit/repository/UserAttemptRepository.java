package com.doit.repository;

import com.doit.entity.Exam;
import com.doit.entity.User;
import com.doit.entity.UserAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAttemptRepository extends JpaRepository<UserAttempt, Long> {

    List<UserAttempt> findByUserOrderByStartedAtDesc(User user);

    Page<UserAttempt> findByUser(User user, Pageable pageable);

    List<UserAttempt> findByUserAndExamSkillOrderByStartedAtDesc(User user, Exam.Skill skill);

    Page<UserAttempt> findByUserAndExamSkill(User user, Exam.Skill skill, Pageable pageable);

    Optional<UserAttempt> findByIdAndUser(Long id, User user);

    List<UserAttempt> findByUserAndStatus(User user, UserAttempt.AttemptStatus status);

    Optional<UserAttempt> findTopByUserAndExamAndStatusOrderByCreatedAtDesc(
            User user, Exam exam, UserAttempt.AttemptStatus status);

    @Query("SELECT ua FROM UserAttempt ua WHERE ua.user = :user AND ua.exam.skill = :skill " +
           "AND ua.status = 'GRADED' ORDER BY ua.submittedAt DESC")
    List<UserAttempt> findGradedAttemptsByUserAndSkill(@Param("user") User user, 
                                                        @Param("skill") Exam.Skill skill);

    @Query("SELECT COUNT(ua) FROM UserAttempt ua WHERE ua.user = :user AND ua.exam.skill = :skill")
    Long countByUserAndSkill(@Param("user") User user, @Param("skill") Exam.Skill skill);

    @Query("SELECT AVG(ua.bandScore) FROM UserAttempt ua WHERE ua.user = :user AND ua.exam.skill = :skill " +
           "AND ua.status = 'GRADED'")
    Double getAverageBandScoreByUserAndSkill(@Param("user") User user, @Param("skill") Exam.Skill skill);

    @Query("SELECT ua FROM UserAttempt ua WHERE ua.user = :user AND ua.exam.skill = :skill " +
           "AND ua.status = 'GRADED' ORDER BY ua.bandScore DESC")
    List<UserAttempt> findTopAttemptsByUserAndSkill(@Param("user") User user, 
                                                     @Param("skill") Exam.Skill skill,
                                                     Pageable pageable);

    List<UserAttempt> findByMockTestId(Long mockTestId);
}
