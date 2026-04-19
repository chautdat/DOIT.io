package com.doit.dto.studyplan;

import com.doit.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanItemDTO {

    private Long id;
    private Exam.Skill skill;
    private Long examId;
    private String activityType;
    private String activityDescription;
    private LocalDate recommendedDate;
    private Boolean isCompleted;
    private LocalDate completedDate;
    private Integer orderNumber;
}
