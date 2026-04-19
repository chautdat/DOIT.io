package com.doit.dto.studyplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanDTO {

    private Long id;
    private Long userId;
    private BigDecimal currentBand;
    private BigDecimal targetBand;
    private LocalDate targetDate;
    private Boolean focusListening;
    private Boolean focusReading;
    private Boolean focusWriting;
    private Boolean focusSpeaking;
    private Integer studyHoursPerDay;
    private Boolean isActive;
    private List<StudyPlanItemDTO> items;
    private Integer totalItems;
    private Integer completedItems;
    private Integer progressPercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
