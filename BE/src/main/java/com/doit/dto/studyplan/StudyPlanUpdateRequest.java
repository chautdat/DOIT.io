package com.doit.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanUpdateRequest {
    private String name;
    private Double targetBand;
    private LocalDate targetDate;
}
