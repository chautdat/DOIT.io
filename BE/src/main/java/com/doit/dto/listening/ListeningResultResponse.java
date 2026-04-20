package com.doit.dto.listening;

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
public class ListeningResultResponse {
    private String attemptId;
    private String examId;
    private String examTitle;
    
    // Scoring
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double score; // Raw score percentage
    private Double bandScore; // IELTS band (1-9)
    
    // Time tracking
    private Integer timeSpentSeconds;
    private Integer timeLimitSeconds;
    
    // Detailed results
    private List<SectionResult> sectionResults;
    
    // Feedback
    private String overallFeedback;
    private List<String> improvementTips;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionResult {
        private Integer partNumber;
        private String partTitle;
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
        private String userAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private String explanation;
    }
}
