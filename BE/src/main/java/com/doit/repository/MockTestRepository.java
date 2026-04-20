package com.doit.repository;

import com.doit.entity.MockTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockTestRepository extends MongoRepository<MockTest, String> {

    List<MockTest> findByUserIdOrderByStartedAtDesc(String userId);

    Page<MockTest> findByUserId(String userId, Pageable pageable);

    Optional<MockTest> findByIdAndUserId(String id, String userId);

    List<MockTest> findByUserIdAndStatus(String userId, MockTest.MockTestStatus status);

    @Query("{ 'userId': ?0, 'status': 'COMPLETED' }")
    List<MockTest> findCompletedMockTestsByUserId(String userId);

    Long countByUserId(String userId);

    Long countByUserIdAndStatus(String userId, MockTest.MockTestStatus status);
}
