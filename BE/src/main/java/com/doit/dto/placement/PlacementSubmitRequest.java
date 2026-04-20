package com.doit.dto.placement;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementSubmitRequest {

    @NotNull(message = "Attempt ID is required")
    private String attemptId;

    // Map of questionId -> answer
    private Map<String, String> answers;

    // For writing section
    private List<WritingAnswer> writingAnswers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WritingAnswer {
        private String questionId;
        private String essay;
    }
}
