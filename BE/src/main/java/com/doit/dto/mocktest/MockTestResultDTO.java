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
public class MockTestResultDTO {

    private String id;
    private Double listeningScore;
    private Double readingScore;
    private Double writingScore;
    private Double speakingScore;
    private Double overallBand;
    private LocalDateTime completedAt;
}
