package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.speaking.*;
import com.doit.entity.Exam;
import com.doit.entity.User;
import com.doit.entity.UserAttempt;
import com.doit.service.SpeakingService;
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
@RequestMapping("/api/v1/speaking")
@RequiredArgsConstructor
public class SpeakingController {

    private final SpeakingService speakingService;

    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<SpeakingExamDTO>>> getSpeakingExams(
            @RequestParam(required = false) Exam.BandLevel bandLevel,
            @RequestParam(required = false) Exam.ExamType examType) {
        List<SpeakingExamDTO> exams = speakingService.getSpeakingExams(bandLevel, examType);
        return ResponseEntity.ok(ApiResponse.success("Speaking exams retrieved successfully", exams));
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<SpeakingExamDTO>> getSpeakingExam(@PathVariable String examId) {
        SpeakingExamDTO exam = speakingService.getSpeakingExam(examId);
        return ResponseEntity.ok(ApiResponse.success("Speaking exam retrieved successfully", exam));
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<UserAttempt>> startAttempt(
            @AuthenticationPrincipal User user,
            @PathVariable String examId) {
        UserAttempt attempt = speakingService.startAttempt(user, examId);
        return ResponseEntity.ok(ApiResponse.success("Speaking attempt started", attempt));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<SpeakingResultDTO>> submitAttempt(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SpeakingSubmitRequest request) {
        SpeakingResultDTO result = speakingService.submitAttempt(user, request);
        return ResponseEntity.ok(ApiResponse.success("Speaking submitted", result));
    }

    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<ApiResponse<SpeakingResultDTO>> getAttemptResult(
            @AuthenticationPrincipal User user,
            @PathVariable String attemptId) {
        SpeakingResultDTO result = speakingService.getAttemptResult(user, attemptId);
        return ResponseEntity.ok(ApiResponse.success("Speaking result retrieved", result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<UserAttempt>>> getUserAttempts(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<UserAttempt> attempts = speakingService.getUserAttempts(user, pageable);
        return ResponseEntity.ok(ApiResponse.success("Speaking history retrieved", attempts));
    }
}
