package com.doit.dto.listening;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningTestResponse {
    private String examId;
    private String title;
    private String description;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private List<SectionResponse> sections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionResponse {
        private String audioId;
        private Integer partNumber;
        private String partTitle;
        private String audioUrl;
        private Integer durationSeconds;
        private List<QuestionResponse> questions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private String questionId;
        private Integer orderNumber;
        private String questionText;
        private String questionType;
        private List<String> options; // For multiple choice
    }
}
