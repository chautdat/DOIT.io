package com.doit.repository;

import com.doit.entity.MockTest;
import com.doit.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockTestRepository extends JpaRepository<MockTest, Long> {

    List<MockTest> findByUserOrderByStartedAtDesc(User user);

    Page<MockTest> findByUser(User user, Pageable pageable);

    Optional<MockTest> findByIdAndUser(Long id, User user);

    List<MockTest> findByUserAndStatus(User user, MockTest.MockTestStatus status);

    @Query("SELECT mt FROM MockTest mt WHERE mt.user = :user AND mt.status = 'GRADED' " +
           "ORDER BY mt.completedAt DESC")
    List<MockTest> findGradedMockTestsByUser(@Param("user") User user);

    @Query("SELECT AVG(mt.overallBand) FROM MockTest mt WHERE mt.user = :user AND mt.status = 'GRADED'")
    Double getAverageOverallBandByUser(@Param("user") User user);

    @Query("SELECT mt FROM MockTest mt WHERE mt.user = :user AND mt.status = 'GRADED' " +
           "ORDER BY mt.overallBand DESC")
    List<MockTest> findTopMockTestsByUser(@Param("user") User user, Pageable pageable);

    Long countByUser(User user);

    Long countByUserAndStatus(User user, MockTest.MockTestStatus status);
}
