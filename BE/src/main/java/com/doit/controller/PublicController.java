package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.entity.Exam;
import com.doit.repository.ExamRepository;
import com.doit.repository.UserAttemptRepository;
import com.doit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Public API Controller - Không cần authentication
 * Dùng cho demo, testing và public data
 */
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final UserAttemptRepository attemptRepository;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", new Date());
        health.put("version", "1.0.0");
        health.put("environment", "development");
        
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", health));
    }

    /**
     * Lấy thông tin tổng quan về platform
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Đếm số exam theo skill
        Map<String, Long> examsBySkill = new HashMap<>();
        for (Exam.Skill skill : Exam.Skill.values()) {
            long count = examRepository.countBySkillAndIsActiveTrue(skill);
            examsBySkill.put(skill.name().toLowerCase(), count);
        }
        stats.put("examsBySkill", examsBySkill);
        
        // Tổng số
        stats.put("totalExams", examRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalAttempts", attemptRepository.count());
        
        // Features
        stats.put("features", Arrays.asList(
                "Full IELTS Mock Tests",
                "AI-Powered Writing Grading",
                "AI-Powered Speaking Grading",
                "Personalized Study Plans",
                "Progress Tracking",
                "Band Score Calculator",
                "Vocabulary Builder"
        ));
        
        return ResponseEntity.ok(ApiResponse.success("Platform statistics", stats));
    }

    /**
     * Lấy danh sách exam types available
     */
    @GetMapping("/exam-types")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExamTypes() {
        Map<String, Object> types = new HashMap<>();
        
        // Skills
        types.put("skills", Arrays.stream(Exam.Skill.values())
                .map(s -> Map.of(
                        "value", s.name(),
                        "label", capitalize(s.name())
                ))
                .collect(Collectors.toList()));
        
        // Band levels
        types.put("bandLevels", Arrays.stream(Exam.BandLevel.values())
                .map(b -> Map.of(
                        "value", b.name(),
                        "label", b.name().replace("_", "-")
                ))
                .collect(Collectors.toList()));
        
        // Exam types
        types.put("examTypes", Arrays.stream(Exam.ExamType.values())
                .map(e -> Map.of(
                        "value", e.name(),
                        "label", capitalize(e.name().replace("_", " "))
                ))
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(ApiResponse.success("Exam types", types));
    }

    /**
     * Lấy sample exams (không cần auth)
     */
    @GetMapping("/sample-exams")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSampleExams() {
        List<Exam> exams = examRepository.findAll();
        
        List<Map<String, Object>> samples = exams.stream()
                .limit(10)
                .map(exam -> {
                    Map<String, Object> sample = new HashMap<>();
                    sample.put("id", exam.getId());
                    sample.put("title", exam.getTitle());
                    sample.put("skill", exam.getSkill());
                    sample.put("bandLevel", exam.getBandLevel());
                    sample.put("durationMinutes", exam.getDurationMinutes());
                    sample.put("totalQuestions", exam.getTotalQuestions());
                    sample.put("description", exam.getDescription());
                    return sample;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Sample exams", samples));
    }

    /**
     * IELTS Band Score Guide
     */
    @GetMapping("/band-guide")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBandGuide() {
        List<Map<String, Object>> guide = new ArrayList<>();
        
        guide.add(createBandInfo(9, "Expert User", 
                "Full operational command of the language. Appropriate, accurate and fluent with complete understanding."));
        guide.add(createBandInfo(8, "Very Good User",
                "Fully operational command with occasional unsystematic inaccuracies. May misunderstand some things in unfamiliar situations."));
        guide.add(createBandInfo(7, "Good User",
                "Operational command with occasional inaccuracies. Generally handles complex language well and understands detailed reasoning."));
        guide.add(createBandInfo(6, "Competent User",
                "Generally effective command despite some inaccuracies. Can use and understand fairly complex language in familiar situations."));
        guide.add(createBandInfo(5, "Modest User",
                "Partial command of the language. Copes with overall meaning in most situations, though likely to make many mistakes."));
        guide.add(createBandInfo(4, "Limited User",
                "Basic competence limited to familiar situations. Frequent problems in understanding and expression."));
        guide.add(createBandInfo(3, "Extremely Limited User",
                "Conveys and understands only general meaning in very familiar situations. Frequent breakdowns in communication."));
        guide.add(createBandInfo(2, "Intermittent User",
                "No real communication possible except the most basic information using isolated words."));
        guide.add(createBandInfo(1, "Non User",
                "Essentially has no ability to use the language beyond a few isolated words."));
        guide.add(createBandInfo(0, "Did not attempt",
                "No assessable information provided."));
        
        return ResponseEntity.ok(ApiResponse.success("IELTS Band Score Guide", guide));
    }

    /**
     * Study tips by skill
     */
    @GetMapping("/study-tips/{skill}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudyTips(
            @PathVariable String skill) {
        
        Map<String, Object> tips = new HashMap<>();
        tips.put("skill", skill.toUpperCase());
        
        switch (skill.toLowerCase()) {
            case "listening":
                tips.put("tips", Arrays.asList(
                        "Practice listening to various English accents",
                        "Read questions before listening starts",
                        "Pay attention to synonyms and paraphrasing",
                        "Practice note-taking while listening",
                        "Focus on key words in questions"
                ));
                tips.put("resources", Arrays.asList(
                        "BBC World Service podcasts",
                        "TED Talks",
                        "IELTS Listening practice tests"
                ));
                break;
                
            case "reading":
                tips.put("tips", Arrays.asList(
                        "Skim the passage before reading in detail",
                        "Focus on understanding the main idea first",
                        "Practice identifying key words and synonyms",
                        "Time yourself while practicing",
                        "Read academic journals and newspapers"
                ));
                tips.put("resources", Arrays.asList(
                        "The Economist",
                        "National Geographic",
                        "Academic research papers"
                ));
                break;
                
            case "writing":
                tips.put("tips", Arrays.asList(
                        "Plan your essay before writing",
                        "Use a variety of sentence structures",
                        "Learn topic-specific vocabulary",
                        "Practice writing under timed conditions",
                        "Review and edit your work"
                ));
                tips.put("resources", Arrays.asList(
                        "IELTS Simon essays",
                        "Model answers from Cambridge books",
                        "Academic writing courses"
                ));
                break;
                
            case "speaking":
                tips.put("tips", Arrays.asList(
                        "Practice speaking English every day",
                        "Record yourself and listen back",
                        "Prepare answers for common topics",
                        "Use a variety of vocabulary and grammar",
                        "Don't memorize full answers"
                ));
                tips.put("resources", Arrays.asList(
                        "Speaking practice with native speakers",
                        "IELTS Speaking sample videos",
                        "Pronunciation apps"
                ));
                break;
                
            default:
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Invalid skill. Use: listening, reading, writing, speaking")
                );
        }
        
        return ResponseEntity.ok(ApiResponse.success("Study tips for " + skill, tips));
    }

    // ===================== HELPER METHODS =====================

    private Map<String, Object> createBandInfo(int band, String level, String description) {
        Map<String, Object> info = new HashMap<>();
        info.put("band", band);
        info.put("level", level);
        info.put("description", description);
        return info;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
