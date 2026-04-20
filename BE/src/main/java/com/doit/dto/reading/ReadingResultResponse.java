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
public class ReadingResultResponse {
    private String attemptId;
    private String examId;
    private String examTitle;
    
    // Scoring
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double score;
    private Double bandScore;
    
    // Time tracking
    private Integer timeSpentSeconds;
    private Integer timeLimitSeconds;
    
    // Detailed results
    private List<PassageResult> passageResults;
    
    // Feedback
    private String overallFeedback;
    private List<String> improvementTips;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassageResult {
        private Integer passageNumber;
        private String passageTitle;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private List<QuestionResult> questionResults;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        private String questionId;
        private Integer orderNumber;
        private String questionText;
        private String questionType;
        private String userAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private String explanation;
    }
}
