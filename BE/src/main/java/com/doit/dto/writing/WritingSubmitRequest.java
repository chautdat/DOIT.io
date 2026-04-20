package com.doit.dto.writing;

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
public class WritingSubmitRequest {
    @NotNull(message = "Exam ID is required")
    private String examId;

    @NotNull(message = "Task ID is required")
    private String taskId;

    @NotBlank(message = "Essay content is required")
    private String essay;

    private Integer timeSpentSeconds;
}
