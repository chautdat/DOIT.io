package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.writing.*;
import com.doit.entity.Exam;
import com.doit.entity.User;
import com.doit.entity.UserAttempt;
import com.doit.entity.WritingSubmission;
import com.doit.service.WritingService;
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
@RequestMapping("/api/v1/writing")
@RequiredArgsConstructor
public class WritingController {

    private final WritingService writingService;

    /**
     * Get list of all writing exams with optional filters
     */
    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<WritingExamDTO>>> getWritingExams(
            @RequestParam(required = false) Exam.BandLevel bandLevel,
            @RequestParam(required = false) Exam.ExamType examType) {
        
        List<WritingExamDTO> exams = writingService.getWritingExams(bandLevel, examType);
        return ResponseEntity.ok(ApiResponse.success("Writing exams retrieved successfully", exams));
    }

    /**
     * Get writing exam details with all tasks
     */
    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<WritingExamDTO>> getWritingExam(@PathVariable Long examId) {
        WritingExamDTO exam = writingService.getWritingExam(examId);
        return ResponseEntity.ok(ApiResponse.success("Writing exam retrieved successfully", exam));
    }

    /**
     * Start a new writing attempt
     */
    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<UserAttempt>> startAttempt(
            @AuthenticationPrincipal User user,
            @PathVariable Long examId) {
        
        UserAttempt attempt = writingService.startAttempt(user, examId);
        return ResponseEntity.ok(ApiResponse.success("Writing attempt started", attempt));
    }

    /**
     * Submit essay for grading (with AI feedback)
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<WritingResultDTO>> submitEssay(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WritingSubmitRequest request) {
        
        WritingResultDTO result = writingService.submitEssay(user, request);
        return ResponseEntity.ok(ApiResponse.success("Essay submitted and graded", result));
    }

    /**
     * Get writing submission result
     */
    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<ApiResponse<WritingResultDTO>> getSubmissionResult(
            @AuthenticationPrincipal User user,
            @PathVariable Long submissionId) {
        
        WritingResultDTO result = writingService.getSubmissionResult(user, submissionId);
        return ResponseEntity.ok(ApiResponse.success("Submission result retrieved", result));
    }

    /**
     * Get user's writing submission history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<WritingSubmission>>> getUserSubmissions(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<WritingSubmission> submissions = writingService.getUserSubmissions(user, pageable);
        return ResponseEntity.ok(ApiResponse.success("Writing history retrieved", submissions));
    }
}
