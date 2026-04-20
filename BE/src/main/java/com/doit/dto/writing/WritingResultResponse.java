package com.doit.dto.writing;

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
public class WritingResultResponse {
    private String submissionId;
    private String examId;
    private String taskId;
    private Integer taskNumber;
    
    // User submission
    private String essay;
    private Integer wordCount;
    private Integer timeSpentSeconds;
    
    // AI Grading (Band 1-9)
    private Double overallBand;
    private Map<String, Double> criteriaScores; // Task Achievement, Coherence, Lexical, Grammar
    
    // Detailed feedback
    private String overallFeedback;
    private List<CriteriaFeedback> criteriaFeedback;
    private List<String> strengths;
    private List<String> areasToImprove;
    private List<GrammarError> grammarErrors;
    private List<String> vocabularySuggestions;
    
    // Model essay for reference
    private String modelEssay;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriteriaFeedback {
        private String criteria;
        private Double band;
        private String feedback;
        private List<String> suggestions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarError {
        private String text;
        private String correction;
        private String explanation;
        private String errorType;
        private Integer position;
    }
}
