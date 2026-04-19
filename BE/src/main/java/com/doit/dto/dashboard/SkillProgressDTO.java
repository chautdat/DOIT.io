package com.doit.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillProgressDTO {

    private String skill;
    private BigDecimal currentBand;
    private BigDecimal previousBand;
    private BigDecimal improvement;
    private Integer totalAttempts;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Double accuracyPercent;
    private Integer practiceHours;
    private String trend; // IMPROVING, STABLE, DECLINING
}
