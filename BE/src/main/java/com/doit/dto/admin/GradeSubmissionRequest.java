package com.doit.dto.admin;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeSubmissionRequest {

    @NotNull(message = "Band score is required")
    @DecimalMin(value = "0.0", message = "Band score must be at least 0.0")
    @DecimalMax(value = "9.0", message = "Band score must be at most 9.0")
    private Double bandScore;
    
    @NotBlank(message = "Feedback is required")
    private String feedback;
    
    private String taskAchievement;      // Writing criteria
    private String coherenceCohesion;
    private String lexicalResource;
    private String grammaticalRange;
    
    private String fluencyCoherence;     // Speaking criteria
    private String pronunciation;
}
