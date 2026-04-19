package com.doit.dto.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingResultDTO {

    private Long attemptId;
    private Long examId;
    private String examTitle;
    private BigDecimal overallBandScore;
    private BigDecimal fluencyCoherenceScore;
    private BigDecimal lexicalResourceScore;
    private BigDecimal grammarAccuracyScore;
    private BigDecimal pronunciationScore;
    private Integer totalDurationSeconds;
    private Integer partsCompleted;
    private Integer totalParts;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<PartResultDTO> partResults;
    private String overallFeedback;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartResultDTO {
        private Long partId;
        private Integer partNumber;
        private String topic;
        private String audioUrl;
        private String transcript;
        private Integer durationSeconds;
        private BigDecimal bandScore;
        private BigDecimal fluencyCoherenceScore;
        private BigDecimal lexicalResourceScore;
        private BigDecimal grammarAccuracyScore;
        private BigDecimal pronunciationScore;
        private String aiFeedback;
        private LocalDateTime submittedAt;
    }
}
