package com.doit.dto.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingResultDTO {

    private String attemptId;
    private String examId;
    private Double bandScore;
    private LocalDateTime submittedAt;
    private List<PartResultDTO> partResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartResultDTO {
        private String partId;
        private Integer partNumber;
        private String topic;
        private String audioUrl;
        private String transcript;
        private Integer durationSeconds;
        private Double bandScore;
        private String aiFeedback;
        private LocalDateTime submittedAt;
    }
}
