package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "study_plan_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanItem {

    @Id
    private String id;

    private String studyPlanId;

    private String examId;

    private String skill;

    private LocalDate scheduledDate;

    @Builder.Default
    private Boolean isCompleted = false;

    private LocalDateTime completedAt;

    private Integer orderIndex;

    @CreatedDate
    private LocalDateTime createdAt;
}
