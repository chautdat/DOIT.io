package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.listening.*;
import com.doit.entity.Exam;
import com.doit.entity.User;
import com.doit.entity.UserAttempt;
import com.doit.repository.UserRepository;
import com.doit.service.ListeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/listening")
@RequiredArgsConstructor
public class ListeningController {

    private final ListeningService listeningService;
    private final UserRepository userRepository;

    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<ListeningExamDTO>>> getExams(
            @RequestParam(required = false) Exam.BandLevel bandLevel,
            @RequestParam(required = false) Exam.ExamType examType) {
        
        List<ListeningExamDTO> exams = listeningService.getListeningExams(bandLevel, examType);
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved successfully", exams));
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<ListeningExamDTO>> getExam(@PathVariable Long examId) {
        ListeningExamDTO exam = listeningService.getListeningExam(examId);
        return ResponseEntity.ok(ApiResponse.success("Exam retrieved successfully", exam));
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<UserAttempt>> startAttempt(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserAttempt attempt = listeningService.startAttempt(user, examId);
        return ResponseEntity.ok(ApiResponse.success("Attempt started successfully", attempt));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ListeningResultDTO>> submitAttempt(
            @Valid @RequestBody ListeningSubmitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ListeningResultDTO result = listeningService.submitAttempt(user, request);
        return ResponseEntity.ok(ApiResponse.success("Exam submitted successfully", result));
    }

    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<ApiResponse<ListeningResultDTO>> getAttemptResult(
            @PathVariable Long attemptId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ListeningResultDTO result = listeningService.getAttemptResult(user, attemptId);
        return ResponseEntity.ok(ApiResponse.success("Attempt result retrieved successfully", result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<UserAttempt>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Page<UserAttempt> attempts = listeningService.getUserAttempts(
                user, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt")));
        
        return ResponseEntity.ok(ApiResponse.success("History retrieved successfully", attempts));
    }
}
