package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.reading.*;
import com.doit.entity.Exam;
import com.doit.entity.User;
import com.doit.entity.UserAttempt;
import com.doit.service.ReadingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    /**
     * Get list of all reading exams with optional filters
     */
    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<ReadingExamDTO>>> getReadingExams(
            @RequestParam(required = false) Exam.BandLevel bandLevel,
            @RequestParam(required = false) Exam.ExamType examType) {
        
        List<ReadingExamDTO> exams = readingService.getReadingExams(bandLevel, examType);
        return ResponseEntity.ok(ApiResponse.success("Reading exams retrieved successfully", exams));
    }

    /**
     * Get reading exam details with all passages and questions
     */
    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<ReadingExamDTO>> getReadingExam(@PathVariable Long examId) {
        ReadingExamDTO exam = readingService.getReadingExam(examId);
        return ResponseEntity.ok(ApiResponse.success("Reading exam retrieved successfully", exam));
    }

    /**
     * Start a new reading attempt
     */
    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<UserAttempt>> startAttempt(
            @AuthenticationPrincipal User user,
            @PathVariable Long examId) {
        
        UserAttempt attempt = readingService.startAttempt(user, examId);
        return ResponseEntity.ok(ApiResponse.success("Reading attempt started", attempt));
    }

    /**
     * Submit reading answers for grading
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ReadingResultDTO>> submitAttempt(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReadingSubmitRequest request) {
        
        ReadingResultDTO result = readingService.submitAttempt(user, request);
        return ResponseEntity.ok(ApiResponse.success("Reading attempt submitted and graded", result));
    }

    /**
     * Get reading attempt result
     */
    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<ApiResponse<ReadingResultDTO>> getAttemptResult(
            @AuthenticationPrincipal User user,
            @PathVariable Long attemptId) {
        
        ReadingResultDTO result = readingService.getAttemptResult(user, attemptId);
        return ResponseEntity.ok(ApiResponse.success("Attempt result retrieved", result));
    }

    /**
     * Get user's reading attempt history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<UserAttempt>>> getUserAttempts(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<UserAttempt> attempts = readingService.getUserAttempts(user, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reading history retrieved", attempts));
    }
}
