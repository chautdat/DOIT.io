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
    private Long attemptId;

    // Map of questionId -> answer
    private Map<Long, String> answers;

    // For writing section
    private List<WritingAnswer> writingAnswers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WritingAnswer {
        private Long questionId;
        private String essay;
    }
}
