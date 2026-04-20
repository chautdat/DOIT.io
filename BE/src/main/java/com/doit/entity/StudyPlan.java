package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "study_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlan {

    @Id
    private String id;

    private String userId;

    private String name;

    private Double targetScore;

    private Double targetBand;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate targetDate;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private List<String> focusSkills = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
