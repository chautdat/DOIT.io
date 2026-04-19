package com.doit.dto.mocktest;

import com.doit.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTestResultDTO {

    private Long mockTestId;
    private Long userId;
    private BigDecimal overallBand;
    private Map<Exam.Skill, BigDecimal> skillBands;
    private Map<Exam.Skill, Long> attemptIds;
    private String overallFeedback;
    private String strengths;
    private String areasForImprovement;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer totalTimeMinutes;
}
