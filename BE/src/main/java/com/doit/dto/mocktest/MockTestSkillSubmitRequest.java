package com.doit.dto.mocktest;

import com.doit.entity.Exam;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTestSkillSubmitRequest {

    @NotNull(message = "Skill is required")
    private Exam.Skill skill;

    @NotNull(message = "Attempt ID is required")
    private Long attemptId;

    private BigDecimal bandScore;
}
