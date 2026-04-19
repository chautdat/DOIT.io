package com.doit.controller;

import com.doit.dto.admin.*;
import com.doit.dto.common.ApiResponse;
import com.doit.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ==================== USER MANAGEMENT ====================
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserAdminDTO>>> getAllUsers(Pageable pageable) {
        Page<UserAdminDTO> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserAdminDTO>> getUserById(@PathVariable Long userId) {
        UserAdminDTO user = adminService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        UserAdminDTO user = adminService.updateUserRole(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", user));
    }
    
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<UserAdminDTO>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        UserAdminDTO user = adminService.updateUserStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    // ==================== EXAM MANAGEMENT ====================
    
    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<Page<ExamAdminDTO>>> getAllExams(Pageable pageable) {
        Page<ExamAdminDTO> exams = adminService.getAllExams(pageable);
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved successfully", exams));
    }
    
    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<ExamAdminDTO>> getExamById(@PathVariable Long examId) {
        ExamAdminDTO exam = adminService.getExamById(examId);
        return ResponseEntity.ok(ApiResponse.success("Exam retrieved successfully", exam));
    }
    
    @PutMapping("/exams/{examId}/toggle-status")
    public ResponseEntity<ApiResponse<ExamAdminDTO>> toggleExamStatus(@PathVariable Long examId) {
        ExamAdminDTO exam = adminService.toggleExamStatus(examId);
        return ResponseEntity.ok(ApiResponse.success("Exam status toggled successfully", exam));
    }

    // ==================== SUBMISSION MANAGEMENT ====================
    
    @GetMapping("/submissions/writing")
    public ResponseEntity<ApiResponse<Page<SubmissionAdminDTO>>> getWritingSubmissions(Pageable pageable) {
        Page<SubmissionAdminDTO> submissions = adminService.getWritingSubmissions(pageable);
        return ResponseEntity.ok(ApiResponse.success("Writing submissions retrieved successfully", submissions));
    }
    
    @GetMapping("/submissions/speaking")
    public ResponseEntity<ApiResponse<Page<SubmissionAdminDTO>>> getSpeakingSubmissions(Pageable pageable) {
        Page<SubmissionAdminDTO> submissions = adminService.getSpeakingSubmissions(pageable);
        return ResponseEntity.ok(ApiResponse.success("Speaking submissions retrieved successfully", submissions));
    }
    
    @PostMapping("/submissions/writing/{submissionId}/grade")
    public ResponseEntity<ApiResponse<SubmissionAdminDTO>> gradeWritingSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request) {
        SubmissionAdminDTO submission = adminService.gradeWritingSubmission(submissionId, request);
        return ResponseEntity.ok(ApiResponse.success("Writing submission graded successfully", submission));
    }
    
    @PostMapping("/submissions/speaking/{submissionId}/grade")
    public ResponseEntity<ApiResponse<SubmissionAdminDTO>> gradeSpeakingSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request) {
        SubmissionAdminDTO submission = adminService.gradeSpeakingSubmission(submissionId, request);
        return ResponseEntity.ok(ApiResponse.success("Speaking submission graded successfully", submission));
    }

    // ==================== STATISTICS ====================
    
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<AdminStatsDTO>> getStatistics() {
        AdminStatsDTO stats = adminService.getAdminStatistics();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }
}
