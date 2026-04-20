package com.doit.dto.mocktest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTestDTO {

    private String id;
    private String userId;
    private String listeningExamId;
    private String readingExamId;
    private String writingExamId;
    private String speakingExamId;
    private String status;
    private Double listeningScore;
    private Double readingScore;
    private Double writingScore;
    private Double speakingScore;
    private Double overallBand;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
