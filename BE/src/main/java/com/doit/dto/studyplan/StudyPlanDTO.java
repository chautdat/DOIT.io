package com.doit.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanDTO {
    private String id;
    private String userId;
    private String name;
    private Double targetBand;
    private LocalDate targetDate;
    private List<String> focusSkills;
    private Boolean isActive;
    private List<StudyPlanItemDTO> items;
    private Integer totalItems;
    private Integer completedItems;
    private Integer progressPercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
