package com.doit.dto.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingTestResponse {
    private String examId;
    private String title;
    private String description;
    private Integer totalParts;
    private Integer estimatedDuration; // minutes
    private List<PartResponse> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartResponse {
        private String partId;
        private Integer partNumber;
        private String partTitle;
        private String description;
        private Integer durationMinutes;
        private List<QuestionResponse> questions;
        // For Part 2
        private String cueCard;
        private Integer prepTimeSeconds;
        private Integer speakingTimeSeconds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private String questionId;
        private Integer orderNumber;
        private String question;
        private String topic;
        private List<String> followUpQuestions;
        private List<String> sampleAnswerPoints;
    }
}
