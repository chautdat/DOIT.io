package com.doit.dto.speaking;

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
public class SpeakingSubmitRequest {

    @NotNull(message = "Attempt ID is required")
    private Long attemptId;

    private List<SpeakingPartAnswer> partAnswers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpeakingPartAnswer {
        
        @NotNull(message = "Part ID is required")
        private Long partId;

        private String audioUrl;

        private String transcript;

        private Integer durationSeconds;
    }
}
