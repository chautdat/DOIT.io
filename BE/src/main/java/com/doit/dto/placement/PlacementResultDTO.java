package com.doit.dto.placement;

import com.doit.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementResultDTO {

    private String attemptId;
    private String userId;
    private Double overallBandScore;
    private Map<Exam.Skill, Double> skillScores;
    private Exam.BandLevel recommendedLevel;
    private String feedback;
    private String strengths;
    private String weaknesses;
    private String studyRecommendations;
    private LocalDateTime completedAt;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer timeTakenSeconds;
}
