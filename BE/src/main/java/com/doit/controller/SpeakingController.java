package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.speaking.*;
import com.doit.entity.Exam;
import com.doit.service.SpeakingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/speaking")
@RequiredArgsConstructor
public class SpeakingController {

    private final SpeakingService speakingService;

    /**
     * Get all Speaking exams with optional filtering
     */
    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<Page<SpeakingExamDTO>>> getSpeakingExams(
            @RequestParam(required = false) Exam.ExamType type,
            @RequestParam(required = false) Exam.BandLevel bandLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<SpeakingExamDTO> exams = speakingService.getSpeakingExams(type, bandLevel, pageable);

        return ResponseEntity.ok(ApiResponse.success("Speaking exams retrieved successfully", exams));
    }

    /**
     * Get Speaking exam by ID with all parts
     */
    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<SpeakingExamDTO>> getSpeakingExamById(@PathVariable Long examId) {
        SpeakingExamDTO exam = speakingService.getSpeakingExamById(examId);
        return ResponseEntity.ok(ApiResponse.success("Speaking exam retrieved successfully", exam));
    }

    /**
     * Start a new Speaking attempt
     */
    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<Long>> startAttempt(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = extractUserId(userDetails);
        Long attemptId = speakingService.startAttempt(examId, userId);

        return ResponseEntity.ok(ApiResponse.success("Speaking attempt started successfully", attemptId));
    }

    /**
     * Submit Speaking responses for grading
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<SpeakingResultDTO>> submitAttempt(
            @Valid @RequestBody SpeakingSubmitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = extractUserId(userDetails);
        SpeakingResultDTO result = speakingService.submitAttempt(request, userId);

        return ResponseEntity.ok(ApiResponse.success("Speaking responses submitted and graded successfully", result));
    }

    /**
     * Get Speaking attempt result
     */
    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<ApiResponse<SpeakingResultDTO>> getAttemptResult(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = extractUserId(userDetails);
        SpeakingResultDTO result = speakingService.getAttemptResult(attemptId, userId);

        return ResponseEntity.ok(ApiResponse.success("Speaking attempt result retrieved successfully", result));
    }

    /**
     * Extract user ID from UserDetails
     */
    private Long extractUserId(UserDetails userDetails) {
        // Assuming UserDetails implementation contains user ID
        // This should match your security configuration
        if (userDetails instanceof com.doit.entity.User) {
            return ((com.doit.entity.User) userDetails).getId();
        }
        throw new IllegalStateException("Unable to extract user ID from security context");
    }
}
