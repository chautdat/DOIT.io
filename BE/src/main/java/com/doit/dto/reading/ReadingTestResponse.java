package com.doit.dto.reading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingTestResponse {
    private String examId;
    private String title;
    private String description;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private List<PassageResponse> passages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassageResponse {
        private String passageId;
        private Integer passageNumber;
        private String title;
        private String content;
        private String source;
        private Integer wordCount;
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
        private List<String> options;
        private String instruction;
    }
}
