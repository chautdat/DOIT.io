package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.service.IeltsDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller cung cấp dữ liệu IELTS thật từ các nguồn uy tín
 * - Từ vựng theo band level (Cambridge Academic Word List)
 * - Speaking topics (IELTS Simon, British Council)
 * - Writing topics (đề thi thật)
 * - Reading question types & strategies
 * - Listening section guides
 */
@RestController
@RequestMapping("/api/v1/ielts")
@RequiredArgsConstructor
public class IeltsResourceController {

    private final IeltsDataService ieltsDataService;

    // ============== VOCABULARY ==============

    /**
     * Lấy danh sách từ vựng IELTS theo band level mục tiêu
     * GET /api/v1/ielts/vocabulary?targetBand=7.0
     */
    @GetMapping("/vocabulary")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getVocabularyByBand(
            @RequestParam(defaultValue = "6.5") double targetBand) {
        List<Map<String, Object>> vocabulary = ieltsDataService.getVocabularyByBand(targetBand);
        return ResponseEntity.ok(ApiResponse.success(
                "IELTS vocabulary for band " + targetBand,
                vocabulary
        ));
    }

    /**
     * Lấy danh sách từ vựng đơn giản (chỉ words, không fetch details)
     */
    @GetMapping("/vocabulary/words")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVocabularyWords(
            @RequestParam(defaultValue = "6.5") double targetBand) {
        List<String> words;
        String source;
        if (targetBand >= 7.0) {
            words = IeltsDataService.ADVANCED_VOCAB_BAND_7_8;
            source = "IELTS Advanced Vocabulary Band 7-8";
        } else {
            words = IeltsDataService.ACADEMIC_WORD_LIST_BAND_6_7;
            source = "Cambridge Academic Word List";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("targetBand", targetBand);
        result.put("source", source);
        result.put("totalWords", words.size());
        result.put("words", words);

        return ResponseEntity.ok(ApiResponse.success("Vocabulary word list", result));
    }

    // ============== SPEAKING ==============

    /**
     * Lấy tất cả Speaking topics theo Part
     * GET /api/v1/ielts/speaking/topics
     */
    @GetMapping("/speaking/topics")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getSpeakingTopics() {
        return ResponseEntity.ok(ApiResponse.success(
                "IELTS Speaking topics by part",
                ieltsDataService.getSpeakingTopics()
        ));
    }

    /**
     * Lấy random một Cue Card topic cho Speaking Part 2
     * GET /api/v1/ielts/speaking/cue-card/random
     */
    @GetMapping("/speaking/cue-card/random")
    public ResponseEntity<ApiResponse<Map<String, String>>> getRandomCueCard() {
        String topic = ieltsDataService.getRandomSpeakingCueCard();
        Map<String, String> result = new HashMap<>();
        result.put("topic", topic);
        result.put("part", "Part 2");
        result.put("prepTime", "1 minute");
        result.put("speakingTime", "1-2 minutes");

        return ResponseEntity.ok(ApiResponse.success("Random Speaking Part 2 Cue Card", result));
    }

    // ============== WRITING ==============

    /**
     * Lấy tất cả Writing Task 2 topics theo category
     * GET /api/v1/ielts/writing/topics
     */
    @GetMapping("/writing/topics")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getWritingTopics() {
        return ResponseEntity.ok(ApiResponse.success(
                "IELTS Writing Task 2 topics by category",
                ieltsDataService.getWritingTopics()
        ));
    }

    /**
     * Lấy random một đề Writing Task 2
     * GET /api/v1/ielts/writing/topic/random?category=Education
     */
    @GetMapping("/writing/topic/random")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRandomWritingTopic(
            @RequestParam(required = false) String category) {
        String topic = ieltsDataService.getRandomWritingTopic(category);
        Map<String, Object> result = new HashMap<>();
        result.put("topic", topic);
        result.put("category", category != null ? category : "Random");
        result.put("task", "Task 2");
        result.put("wordLimit", "At least 250 words");
        result.put("timeLimit", "40 minutes");

        return ResponseEntity.ok(ApiResponse.success("Random Writing Task 2 topic", result));
    }

    // ============== READING ==============

    /**
     * Lấy thông tin các loại câu hỏi Reading và strategies
     * GET /api/v1/ielts/reading/question-types
     */
    @GetMapping("/reading/question-types")
    public ResponseEntity<ApiResponse<Map<String, Map<String, String>>>> getReadingQuestionTypes() {
        return ResponseEntity.ok(ApiResponse.success(
                "IELTS Reading question types and strategies",
                ieltsDataService.getReadingQuestionTypes()
        ));
    }

    // ============== LISTENING ==============

    /**
     * Lấy thông tin các Section trong Listening
     * GET /api/v1/ielts/listening/sections
     */
    @GetMapping("/listening/sections")
    public ResponseEntity<ApiResponse<Map<String, Map<String, Object>>>> getListeningSections() {
        return ResponseEntity.ok(ApiResponse.success(
                "IELTS Listening section guide",
                ieltsDataService.getListeningSections()
        ));
    }

    // ============== BAND SCORES ==============

    /**
     * Lấy mô tả Band Score theo official IELTS criteria
     * GET /api/v1/ielts/band-descriptors
     */
    @GetMapping("/band-descriptors")
    public ResponseEntity<ApiResponse<Map<Double, Map<String, String>>>> getBandDescriptors() {
        return ResponseEntity.ok(ApiResponse.success(
                "Official IELTS Band Score Descriptors",
                ieltsDataService.getBandDescriptors()
        ));
    }

    /**
     * Lấy mô tả cho một band cụ thể
     * GET /api/v1/ielts/band-descriptors/7.0
     */
    @GetMapping("/band-descriptors/{band}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBandDescriptor(@PathVariable double band) {
        Map<Double, Map<String, String>> descriptors = ieltsDataService.getBandDescriptors();
        Map<String, String> descriptor = descriptors.get(band);
        
        if (descriptor != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("band", band);
            result.putAll(descriptor);
            return ResponseEntity.ok(ApiResponse.success("Band " + band + " descriptor", result));
        }
        
        return ResponseEntity.notFound().build();
    }

    // ============== OVERVIEW ==============

    /**
     * Tổng quan về tất cả resources có sẵn
     * GET /api/v1/ielts/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        overview.put("vocabulary", Map.of(
            "band6_7_words", IeltsDataService.ACADEMIC_WORD_LIST_BAND_6_7.size(),
            "band7_8_words", IeltsDataService.ADVANCED_VOCAB_BAND_7_8.size(),
            "source", "Cambridge Academic Word List"
        ));
        
        overview.put("speaking", Map.of(
            "part1_topics", IeltsDataService.SPEAKING_TOPICS.get("Part 1 - Personal").size(),
            "part2_cue_cards", IeltsDataService.SPEAKING_TOPICS.get("Part 2 - Cue Card Topics").size(),
            "part3_discussions", IeltsDataService.SPEAKING_TOPICS.get("Part 3 - Discussion Topics").size(),
            "source", "IELTS Simon, British Council"
        ));
        
        int totalWritingTopics = ieltsDataService.getWritingTopics().values().stream()
                .mapToInt(List::size)
                .sum();
        overview.put("writing", Map.of(
            "total_topics", totalWritingTopics,
            "categories", ieltsDataService.getWritingTopics().keySet(),
            "source", "IELTS Liz, IELTS Simon"
        ));
        
        overview.put("reading", Map.of(
            "question_types", ieltsDataService.getReadingQuestionTypes().size(),
            "includes", "strategies and tips"
        ));
        
        overview.put("listening", Map.of(
            "sections", ieltsDataService.getListeningSections().size(),
            "includes", "context, difficulty, tips"
        ));
        
        overview.put("band_descriptors", Map.of(
            "bands_covered", ieltsDataService.getBandDescriptors().keySet(),
            "source", "Official IELTS Band Descriptors"
        ));

        return ResponseEntity.ok(ApiResponse.success("IELTS Resources Overview", overview));
    }
}
