package com.doit.dto.studyplan;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class StudyPlanCreateRequest {

    @DecimalMin(value = "1.0", message = "Current band must be at least 1.0")
    @DecimalMax(value = "9.0", message = "Current band cannot exceed 9.0")
    private BigDecimal currentBand;

    @NotNull(message = "Target band is required")
    @DecimalMin(value = "1.0", message = "Target band must be at least 1.0")
    @DecimalMax(value = "9.0", message = "Target band cannot exceed 9.0")
    private BigDecimal targetBand;

    private LocalDate targetDate;

    @Builder.Default
    private Boolean focusListening = true;

    @Builder.Default
    private Boolean focusReading = true;

    @Builder.Default
    private Boolean focusWriting = true;

    @Builder.Default
    private Boolean focusSpeaking = true;

    @Builder.Default
    private Integer studyHoursPerDay = 2;
}
