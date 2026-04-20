package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "writing_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingSubmission {

    @Id
    private String id;

    private String userId;

    private String taskId;

    private String attemptId;

    private String submittedText;

    private Integer wordCount;

    private Double taskAchievementScore;

    private Double coherenceCohesionScore;

    private Double lexicalResourceScore;

    private Double grammaticalRangeScore;

    private Double overallBandScore;

    private String feedback;

    private SubmissionStatus status;

    @CreatedDate
    private LocalDateTime submittedAt;

    @LastModifiedDate
    private LocalDateTime gradedAt;

    public enum SubmissionStatus {
        SUBMITTED,
        GRADING,
        GRADED
    }
}
