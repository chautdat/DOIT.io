package com.doit.dto.admin;

import com.doit.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAdminDTO {

    private Long id;
    private String title;
    private String description;
    private Exam.ExamType type;
    private Exam.Skill skill;
    private Exam.BandLevel bandLevel;
    private Integer timeLimitMinutes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long totalAttempts;
    private Long passCount;
    private Double averageScore;
}
