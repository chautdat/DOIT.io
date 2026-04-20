package com.doit.dto.speaking;

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
public class SpeakingResultResponse {
    private String submissionId;
    private String examId;
    private String partId;
    private Integer partNumber;
    
    // User submission
    private String audioUrl;
    private String transcript; // Speech-to-text result
    private Integer durationSeconds;
    
    // AI Grading (Band 1-9)
    private Double overallBand;
    private Map<String, Double> criteriaScores; // Fluency, Lexical, Grammar, Pronunciation
    
    // Detailed feedback
    private String overallFeedback;
    private List<CriteriaFeedback> criteriaFeedback;
    private List<String> strengths;
    private List<String> areasToImprove;
    
    // Pronunciation feedback
    private List<PronunciationError> pronunciationErrors;
    private List<String> vocabularySuggestions;
    
    // Fluency metrics
    private Double wordsPerMinute;
    private Integer fillerWordsCount;
    private List<String> fillerWordsUsed;

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
    public static class PronunciationError {
        private String word;
        private String userPronunciation;
        private String correctPronunciation;
        private String audioUrl;
        private String tip;
    }
}
