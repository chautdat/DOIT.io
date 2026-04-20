package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_attempts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAttempt {

    @Id
    private String id;

    private String userId;

    private String examId;

    private String skill;

    private String mockTestId;

    @Builder.Default
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @Builder.Default
    private Double score = 0.0;

    @Builder.Default
    private Double bandScore = 0.0;

    @Builder.Default
    private Integer correctAnswers = 0;

    @Builder.Default
    private Integer totalQuestions = 0;

    private Integer timeSpentSeconds;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum AttemptStatus {
        IN_PROGRESS,
        SUBMITTED,
        GRADED,
        COMPLETED,
        ABANDONED
    }
}
