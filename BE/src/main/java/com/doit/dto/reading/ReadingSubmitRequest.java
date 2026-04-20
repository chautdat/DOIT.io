package com.doit.dto.reading;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingSubmitRequest {
    @NotNull(message = "Exam ID is required")
    private String examId;

    @NotNull(message = "Attempt ID is required")
    private String attemptId;

    @NotEmpty(message = "Answers are required")
    private List<ReadingAnswerRequest> answers;

    private Integer timeSpentSeconds;
}
