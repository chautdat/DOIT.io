package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "speaking_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingSubmission {

    @Id
    private String id;

    private String userId;

    private String partId;

    private String attemptId;

    private String audioUrl;

    private String transcript;

    private Integer durationSeconds;

    private Double fluencyCoherenceScore;

    private Double lexicalResourceScore;

    private Double grammaticalRangeScore;

    private Double pronunciationScore;

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
