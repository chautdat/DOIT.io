package com.doit.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private String attemptId;
    private String examId;
    private String examTitle;
    private String skill;
    private Double bandScore;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
