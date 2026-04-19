package com.doit.dto.listening;

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
public class ListeningSubmitRequest {
    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotEmpty(message = "Answers cannot be empty")
    private List<ListeningAnswerRequest> answers;

    private Integer timeSpentSeconds;
}
