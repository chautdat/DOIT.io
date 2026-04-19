package com.doit.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanUpdateRequest {

    private BigDecimal targetBand;
    private LocalDate targetDate;
    private Boolean focusListening;
    private Boolean focusReading;
    private Boolean focusWriting;
    private Boolean focusSpeaking;
    private Integer studyHoursPerDay;
}
