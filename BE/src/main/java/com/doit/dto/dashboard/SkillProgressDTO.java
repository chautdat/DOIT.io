package com.doit.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillProgressDTO {

    private String skill;
    private Double currentBand;
    private Double previousBand;
    private Double improvement;
    private Integer totalAttempts;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Double accuracyPercent;
    private Integer practiceHours;
    private String trend; // IMPROVING, STABLE, DECLINING
}
