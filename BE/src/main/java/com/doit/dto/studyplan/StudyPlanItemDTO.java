package com.doit.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanItemDTO {
    private String id;
    private String skill;
    private String examId;
    private LocalDate scheduledDate;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private Integer orderIndex;
}
