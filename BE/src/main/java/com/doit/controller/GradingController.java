package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.service.IeltsGradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller để test IELTS Grading service
 * Public endpoints cho việc demo/test
 */
@RestController
@RequestMapping("/api/v1/grading")
@RequiredArgsConstructor
public class GradingController {

    private final IeltsGradingService gradingService;

    /**
     * Calculate Listening band score từ số câu đúng
     * GET /api/v1/grading/listening/band?correct=30
     */
    @GetMapping("/listening/band")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateListeningBand(
            @RequestParam int correct) {
        double band = gradingService.calculateListeningBand(correct);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalQuestions", 40);
        result.put("correctAnswers", correct);
        result.put("bandScore", band);
        result.put("description", getBandDescription(band));
        
        return ResponseEntity.ok(ApiResponse.success("Listening band calculated", result));
    }

    /**
     * Calculate Reading Academic band score từ số câu đúng
     */
    @GetMapping("/reading/academic/band")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateReadingAcademicBand(
            @RequestParam int correct) {
        double band = gradingService.calculateReadingAcademicBand(correct);
        
        Map<String, Object> result = new HashMap<>();
        result.put("testType", "Academic");
        result.put("totalQuestions", 40);
        result.put("correctAnswers", correct);
        result.put("bandScore", band);
        result.put("description", getBandDescription(band));
        
        return ResponseEntity.ok(ApiResponse.success("Reading Academic band calculated", result));
    }

    /**
     * Calculate Reading General band score
     */
    @GetMapping("/reading/general/band")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateReadingGeneralBand(
            @RequestParam int correct) {
        double band = gradingService.calculateReadingGeneralBand(correct);
        
        Map<String, Object> result = new HashMap<>();
        result.put("testType", "General Training");
        result.put("totalQuestions", 40);
        result.put("correctAnswers", correct);
        result.put("bandScore", band);
        result.put("description", getBandDescription(band));
        
        return ResponseEntity.ok(ApiResponse.success("Reading General band calculated", result));
    }

    /**
     * Grade Writing Task 2 essay
     * POST /api/v1/grading/writing/task2
     */
    @PostMapping("/writing/task2")
    public ResponseEntity<ApiResponse<Map<String, Object>>> gradeWritingTask2(
            @RequestBody Map<String, String> request) {
        String essay = request.get("essay");
        if (essay == null || essay.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Essay content is required")
            );
        }
        
        Map<String, Object> result = gradingService.gradeWritingTask2(essay);
        return ResponseEntity.ok(ApiResponse.success("Writing Task 2 graded", result));
    }

    /**
     * Grade Speaking based on transcript
     * POST /api/v1/grading/speaking
     */
    @PostMapping("/speaking")
    public ResponseEntity<ApiResponse<Map<String, Object>>> gradeSpeaking(
            @RequestBody Map<String, Object> request) {
        String transcript = (String) request.get("transcript");
        Integer duration = (Integer) request.getOrDefault("durationSeconds", 120);
        
        if (transcript == null || transcript.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Transcript is required")
            );
        }
        
        Map<String, Object> result = gradingService.gradeSpeaking(transcript, duration);
        return ResponseEntity.ok(ApiResponse.success("Speaking graded", result));
    }

    /**
     * Calculate overall band from 4 skills
     */
    @GetMapping("/overall")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateOverallBand(
            @RequestParam double listening,
            @RequestParam double reading,
            @RequestParam double writing,
            @RequestParam double speaking) {
        
        double overall = gradingService.calculateOverallBand(listening, reading, writing, speaking);
        
        Map<String, Object> result = new HashMap<>();
        result.put("listening", listening);
        result.put("reading", reading);
        result.put("writing", writing);
        result.put("speaking", speaking);
        result.put("overall", overall);
        result.put("description", getBandDescription(overall));
        
        return ResponseEntity.ok(ApiResponse.success("Overall band calculated", result));
    }

    /**
     * Get band score conversion table
     */
    @GetMapping("/conversion-table")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConversionTable() {
        Map<String, Object> table = new HashMap<>();
        
        // Listening conversion
        Map<String, Double> listeningTable = new HashMap<>();
        for (int i = 0; i <= 40; i += 5) {
            listeningTable.put(i + " correct", gradingService.calculateListeningBand(i));
        }
        table.put("listening", listeningTable);
        
        // Reading Academic conversion
        Map<String, Double> readingAcademicTable = new HashMap<>();
        for (int i = 0; i <= 40; i += 5) {
            readingAcademicTable.put(i + " correct", gradingService.calculateReadingAcademicBand(i));
        }
        table.put("readingAcademic", readingAcademicTable);
        
        // Reading General conversion
        Map<String, Double> readingGeneralTable = new HashMap<>();
        for (int i = 0; i <= 40; i += 5) {
            readingGeneralTable.put(i + " correct", gradingService.calculateReadingGeneralBand(i));
        }
        table.put("readingGeneral", readingGeneralTable);
        
        return ResponseEntity.ok(ApiResponse.success("Band conversion tables", table));
    }

    private String getBandDescription(double band) {
        if (band >= 9.0) return "Expert";
        if (band >= 8.0) return "Very Good";
        if (band >= 7.0) return "Good";
        if (band >= 6.0) return "Competent";
        if (band >= 5.0) return "Modest";
        if (band >= 4.0) return "Limited";
        return "Extremely Limited";
    }
}
