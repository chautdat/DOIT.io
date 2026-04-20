package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "mock_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTest {

    @Id
    private String id;

    private String userId;

    private String listeningExamId;

    private String readingExamId;

    private String writingExamId;

    private String speakingExamId;

    @Builder.Default
    private MockTestStatus status = MockTestStatus.IN_PROGRESS;

    @Builder.Default
    private Double totalScore = 0.0;

    @Builder.Default
    private Double overallBand = 0.0;

    private Double listeningScore;

    private Double readingScore;

    private Double writingScore;

    private Double speakingScore;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum MockTestStatus {
        IN_PROGRESS,
        COMPLETED,
        GRADED,
        CANCELLED
    }
}
