package com.doit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service để chấm điểm tự động cho IELTS
 * Sử dụng các tiêu chí chấm điểm chính thức của IELTS
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IeltsGradingService {

    // ============== LISTENING/READING GRADING ==============
    
    /**
     * Tính band score cho Listening dựa trên số câu đúng (40 câu)
     * Theo bảng quy đổi chính thức của IELTS
     */
    public double calculateListeningBand(int correctAnswers) {
        if (correctAnswers >= 39) return 9.0;
        if (correctAnswers >= 37) return 8.5;
        if (correctAnswers >= 35) return 8.0;
        if (correctAnswers >= 32) return 7.5;
        if (correctAnswers >= 30) return 7.0;
        if (correctAnswers >= 26) return 6.5;
        if (correctAnswers >= 23) return 6.0;
        if (correctAnswers >= 18) return 5.5;
        if (correctAnswers >= 16) return 5.0;
        if (correctAnswers >= 13) return 4.5;
        if (correctAnswers >= 11) return 4.0;
        if (correctAnswers >= 8) return 3.5;
        if (correctAnswers >= 6) return 3.0;
        if (correctAnswers >= 4) return 2.5;
        return 2.0;
    }

    /**
     * Tính band score cho Reading Academic dựa trên số câu đúng (40 câu)
     */
    public double calculateReadingAcademicBand(int correctAnswers) {
        if (correctAnswers >= 39) return 9.0;
        if (correctAnswers >= 37) return 8.5;
        if (correctAnswers >= 35) return 8.0;
        if (correctAnswers >= 33) return 7.5;
        if (correctAnswers >= 30) return 7.0;
        if (correctAnswers >= 27) return 6.5;
        if (correctAnswers >= 23) return 6.0;
        if (correctAnswers >= 19) return 5.5;
        if (correctAnswers >= 15) return 5.0;
        if (correctAnswers >= 13) return 4.5;
        if (correctAnswers >= 10) return 4.0;
        if (correctAnswers >= 8) return 3.5;
        if (correctAnswers >= 6) return 3.0;
        if (correctAnswers >= 4) return 2.5;
        return 2.0;
    }

    /**
     * Tính band score cho Reading General Training
     */
    public double calculateReadingGeneralBand(int correctAnswers) {
        if (correctAnswers >= 40) return 9.0;
        if (correctAnswers >= 39) return 8.5;
        if (correctAnswers >= 37) return 8.0;
        if (correctAnswers >= 36) return 7.5;
        if (correctAnswers >= 34) return 7.0;
        if (correctAnswers >= 32) return 6.5;
        if (correctAnswers >= 30) return 6.0;
        if (correctAnswers >= 27) return 5.5;
        if (correctAnswers >= 23) return 5.0;
        if (correctAnswers >= 19) return 4.5;
        if (correctAnswers >= 15) return 4.0;
        if (correctAnswers >= 12) return 3.5;
        if (correctAnswers >= 9) return 3.0;
        if (correctAnswers >= 6) return 2.5;
        return 2.0;
    }

    // ============== WRITING GRADING ==============

    /**
     * Chấm điểm Writing Task 2 (Essay)
     * Trả về map với band scores cho từng criteria
     */
    public Map<String, Object> gradeWritingTask2(String essay) {
        Map<String, Object> result = new HashMap<>();
        
        // Word count check
        int wordCount = countWords(essay);
        result.put("wordCount", wordCount);
        
        // Basic analysis
        Map<String, Double> criteriaScores = new HashMap<>();
        
        // Task Achievement (TA)
        double taScore = analyzeTaskAchievement(essay, wordCount);
        criteriaScores.put("taskAchievement", taScore);
        
        // Coherence & Cohesion (CC)
        double ccScore = analyzeCoherenceCohesion(essay);
        criteriaScores.put("coherenceCohesion", ccScore);
        
        // Lexical Resource (LR)
        double lrScore = analyzeLexicalResource(essay);
        criteriaScores.put("lexicalResource", lrScore);
        
        // Grammatical Range & Accuracy (GRA)
        double graScore = analyzeGrammar(essay);
        criteriaScores.put("grammaticalRange", graScore);
        
        result.put("criteriaScores", criteriaScores);
        
        // Overall band (average of 4 criteria, rounded to nearest 0.5)
        double average = (taScore + ccScore + lrScore + graScore) / 4.0;
        double overallBand = Math.round(average * 2) / 2.0;
        result.put("overallBand", overallBand);
        
        // Generate feedback
        result.put("feedback", generateWritingFeedback(criteriaScores, wordCount));
        result.put("strengths", identifyStrengths(essay, criteriaScores));
        result.put("improvements", identifyImprovements(criteriaScores));
        
        return result;
    }

    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }

    private double analyzeTaskAchievement(String essay, int wordCount) {
        double score = 5.0; // Base score
        
        // Word count penalty
        if (wordCount < 250) {
            score -= 1.0;
        } else if (wordCount >= 300) {
            score += 0.5;
        }
        
        // Check for introduction and conclusion
        String lowerEssay = essay.toLowerCase();
        if (lowerEssay.contains("in conclusion") || lowerEssay.contains("to conclude") || 
            lowerEssay.contains("to sum up") || lowerEssay.contains("in summary")) {
            score += 0.5;
        }
        
        // Check for thesis statement indicators
        if (lowerEssay.contains("i believe") || lowerEssay.contains("in my opinion") ||
            lowerEssay.contains("i think") || lowerEssay.contains("this essay will")) {
            score += 0.5;
        }
        
        // Check for examples
        if (lowerEssay.contains("for example") || lowerEssay.contains("for instance") ||
            lowerEssay.contains("such as")) {
            score += 0.5;
        }
        
        return Math.min(9.0, Math.max(1.0, score));
    }

    private double analyzeCoherenceCohesion(String essay) {
        double score = 5.0;
        String lowerEssay = essay.toLowerCase();
        
        // Paragraph structure
        int paragraphs = essay.split("\n\n").length;
        if (paragraphs >= 4) {
            score += 1.0;
        } else if (paragraphs >= 3) {
            score += 0.5;
        }
        
        // Linking words
        List<String> linkingWords = Arrays.asList(
            "however", "moreover", "furthermore", "nevertheless", "therefore",
            "consequently", "in addition", "on the other hand", "firstly",
            "secondly", "finally", "although", "despite", "whereas"
        );
        
        int linkingCount = 0;
        for (String word : linkingWords) {
            if (lowerEssay.contains(word)) linkingCount++;
        }
        
        if (linkingCount >= 6) score += 1.0;
        else if (linkingCount >= 4) score += 0.5;
        
        return Math.min(9.0, Math.max(1.0, score));
    }

    private double analyzeLexicalResource(String essay) {
        double score = 5.0;
        String[] words = essay.toLowerCase().split("\\s+");
        
        // Vocabulary diversity (unique words / total words)
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        double diversity = (double) uniqueWords.size() / words.length;
        
        if (diversity > 0.7) score += 1.5;
        else if (diversity > 0.6) score += 1.0;
        else if (diversity > 0.5) score += 0.5;
        
        // Academic vocabulary
        List<String> academicWords = Arrays.asList(
            "significant", "furthermore", "consequently", "demonstrate", "establish",
            "evaluate", "indicate", "perceive", "sufficient", "phenomenon"
        );
        
        int academicCount = 0;
        for (String word : academicWords) {
            if (essay.toLowerCase().contains(word)) academicCount++;
        }
        
        if (academicCount >= 5) score += 1.0;
        else if (academicCount >= 3) score += 0.5;
        
        return Math.min(9.0, Math.max(1.0, score));
    }

    private double analyzeGrammar(String essay) {
        double score = 5.5;
        
        // Check sentence variety
        String[] sentences = essay.split("[.!?]+");
        if (sentences.length > 0) {
            int totalLength = 0;
            for (String sentence : sentences) {
                totalLength += sentence.trim().split("\\s+").length;
            }
            double avgLength = (double) totalLength / sentences.length;
            
            // Good range is 15-25 words per sentence
            if (avgLength >= 15 && avgLength <= 25) {
                score += 0.5;
            }
        }
        
        // Check for complex structures
        String lowerEssay = essay.toLowerCase();
        List<String> complexStructures = Arrays.asList(
            "which", "that", "although", "while", "whereas", "if", "unless",
            "provided that", "in order to", "so that"
        );
        
        int complexCount = 0;
        for (String structure : complexStructures) {
            if (lowerEssay.contains(structure)) complexCount++;
        }
        
        if (complexCount >= 5) score += 1.0;
        else if (complexCount >= 3) score += 0.5;
        
        return Math.min(9.0, Math.max(1.0, score));
    }

    private String generateWritingFeedback(Map<String, Double> scores, int wordCount) {
        StringBuilder feedback = new StringBuilder();
        
        if (wordCount < 250) {
            feedback.append("Your essay is under the minimum word count of 250 words. ");
        }
        
        double ta = scores.get("taskAchievement");
        if (ta < 6.0) {
            feedback.append("Focus on addressing all parts of the question and developing your ideas more fully. ");
        }
        
        double cc = scores.get("coherenceCohesion");
        if (cc < 6.0) {
            feedback.append("Use more linking words and organize your essay into clear paragraphs. ");
        }
        
        double lr = scores.get("lexicalResource");
        if (lr < 6.0) {
            feedback.append("Try to use a wider range of vocabulary and avoid repetition. ");
        }
        
        double gra = scores.get("grammaticalRange");
        if (gra < 6.0) {
            feedback.append("Practice using complex sentence structures and check for grammatical accuracy. ");
        }
        
        if (feedback.length() == 0) {
            feedback.append("Good work! Continue practicing to maintain and improve your writing skills.");
        }
        
        return feedback.toString().trim();
    }

    private List<String> identifyStrengths(String essay, Map<String, Double> scores) {
        List<String> strengths = new ArrayList<>();
        
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            if (entry.getValue() >= 7.0) {
                switch (entry.getKey()) {
                    case "taskAchievement":
                        strengths.add("Good task response with clear position and developed ideas");
                        break;
                    case "coherenceCohesion":
                        strengths.add("Well-organized essay with effective use of linking words");
                        break;
                    case "lexicalResource":
                        strengths.add("Wide range of vocabulary used appropriately");
                        break;
                    case "grammaticalRange":
                        strengths.add("Good variety of complex sentence structures");
                        break;
                }
            }
        }
        
        if (strengths.isEmpty()) {
            strengths.add("Shows understanding of the essay format");
        }
        
        return strengths;
    }

    private List<String> identifyImprovements(Map<String, Double> scores) {
        List<String> improvements = new ArrayList<>();
        
        double lowest = Collections.min(scores.values());
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            if (entry.getValue() == lowest || entry.getValue() < 6.0) {
                switch (entry.getKey()) {
                    case "taskAchievement":
                        improvements.add("Develop your ideas more fully with specific examples");
                        improvements.add("Make sure to address all parts of the question");
                        break;
                    case "coherenceCohesion":
                        improvements.add("Use more paragraphs to organize your ideas");
                        improvements.add("Include more linking words (however, furthermore, consequently)");
                        break;
                    case "lexicalResource":
                        improvements.add("Learn more academic vocabulary for IELTS essays");
                        improvements.add("Avoid repeating the same words - use synonyms");
                        break;
                    case "grammaticalRange":
                        improvements.add("Practice using complex sentences with relative clauses");
                        improvements.add("Check subject-verb agreement and tense consistency");
                        break;
                }
            }
        }
        
        return improvements;
    }

    // ============== SPEAKING GRADING ==============

    /**
     * Chấm điểm Speaking dựa trên transcript
     */
    public Map<String, Object> gradeSpeaking(String transcript, int durationSeconds) {
        Map<String, Object> result = new HashMap<>();
        
        int wordCount = countWords(transcript);
        double wordsPerMinute = (wordCount * 60.0) / durationSeconds;
        
        result.put("wordCount", wordCount);
        result.put("wordsPerMinute", wordsPerMinute);
        
        Map<String, Double> criteriaScores = new HashMap<>();
        
        // Fluency & Coherence
        double fcScore = analyzeFluencyCoherence(transcript, wordsPerMinute);
        criteriaScores.put("fluencyCoherence", fcScore);
        
        // Lexical Resource
        double lrScore = analyzeLexicalResource(transcript);
        criteriaScores.put("lexicalResource", lrScore);
        
        // Grammatical Range & Accuracy
        double graScore = analyzeGrammar(transcript);
        criteriaScores.put("grammaticalRange", graScore);
        
        // Pronunciation (basic analysis - would need audio for real assessment)
        double pronScore = 6.0; // Default - needs audio analysis
        criteriaScores.put("pronunciation", pronScore);
        
        result.put("criteriaScores", criteriaScores);
        
        // Overall band
        double average = criteriaScores.values().stream().mapToDouble(d -> d).average().orElse(5.0);
        double overallBand = Math.round(average * 2) / 2.0;
        result.put("overallBand", overallBand);
        
        // Filler words analysis
        result.put("fillerWords", analyzeFillerWords(transcript));
        
        // Feedback
        result.put("feedback", generateSpeakingFeedback(criteriaScores, wordsPerMinute));
        
        return result;
    }

    private double analyzeFluencyCoherence(String transcript, double wpm) {
        double score = 5.0;
        
        // Words per minute (native speakers: 120-150 wpm, good IELTS: 100-130)
        if (wpm >= 100 && wpm <= 150) {
            score += 1.0;
        } else if (wpm >= 80 && wpm < 100) {
            score += 0.5;
        } else if (wpm < 60) {
            score -= 0.5;
        }
        
        // Check for filler words (indicates hesitation)
        Map<String, Object> fillerAnalysis = analyzeFillerWords(transcript);
        int fillerCount = (int) fillerAnalysis.get("count");
        int wordCount = countWords(transcript);
        double fillerRatio = (double) fillerCount / wordCount;
        
        if (fillerRatio < 0.02) score += 0.5;
        else if (fillerRatio > 0.05) score -= 0.5;
        
        return Math.min(9.0, Math.max(1.0, score));
    }

    private Map<String, Object> analyzeFillerWords(String transcript) {
        Map<String, Object> result = new HashMap<>();
        
        List<String> fillerWords = Arrays.asList(
            "um", "uh", "er", "ah", "like", "you know", "basically",
            "actually", "literally", "i mean", "sort of", "kind of"
        );
        
        String lower = transcript.toLowerCase();
        List<String> foundFillers = new ArrayList<>();
        int count = 0;
        
        for (String filler : fillerWords) {
            int occurrences = countOccurrences(lower, filler);
            if (occurrences > 0) {
                count += occurrences;
                foundFillers.add(filler + " (" + occurrences + ")");
            }
        }
        
        result.put("count", count);
        result.put("fillers", foundFillers);
        
        return result;
    }

    private int countOccurrences(String text, String word) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }

    private String generateSpeakingFeedback(Map<String, Double> scores, double wpm) {
        StringBuilder feedback = new StringBuilder();
        
        if (wpm < 80) {
            feedback.append("Try to speak more fluently without long pauses. ");
        } else if (wpm > 160) {
            feedback.append("Slow down a bit to ensure clarity. ");
        }
        
        double fc = scores.get("fluencyCoherence");
        if (fc < 6.0) {
            feedback.append("Work on connecting your ideas more smoothly. ");
        }
        
        double lr = scores.get("lexicalResource");
        if (lr < 6.0) {
            feedback.append("Expand your vocabulary range. ");
        }
        
        if (feedback.length() == 0) {
            feedback.append("Good speaking performance! Continue practicing regularly.");
        }
        
        return feedback.toString().trim();
    }

    // ============== OVERALL SCORE CALCULATION ==============

    /**
     * Tính overall band score từ 4 skills
     */
    public double calculateOverallBand(double listening, double reading, double writing, double speaking) {
        double average = (listening + reading + writing + speaking) / 4.0;
        // Round to nearest 0.5
        return Math.round(average * 2) / 2.0;
    }
}
