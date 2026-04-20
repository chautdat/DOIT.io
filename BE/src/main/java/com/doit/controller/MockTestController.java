package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.mocktest.MockTestCreateRequest;
import com.doit.dto.mocktest.MockTestDTO;
import com.doit.dto.mocktest.MockTestResultDTO;
import com.doit.dto.mocktest.MockTestSkillSubmitRequest;
import com.doit.entity.User;
import com.doit.service.MockTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mock-test")
@RequiredArgsConstructor
public class MockTestController {

    private final MockTestService mockTestService;

    /**
     * Start a new mock test
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<MockTestDTO>> startMockTest(
            @AuthenticationPrincipal User user) {
        MockTestDTO mockTest = mockTestService.startMockTest(user);
        return ResponseEntity.ok(ApiResponse.success("Mock test started successfully", mockTest));
    }

    /**
     * Start a mock test with pre-selected exams
     */
    @PostMapping("/start-with-exams")
    public ResponseEntity<ApiResponse<MockTestDTO>> startMockTestWithExams(
            @AuthenticationPrincipal User user,
            @RequestBody MockTestCreateRequest request) {
        MockTestDTO mockTest = mockTestService.startMockTestWithExams(user, request);
        return ResponseEntity.ok(ApiResponse.success("Mock test started with selected exams", mockTest));
    }

    /**
     * Get mock test by ID
     */
    @GetMapping("/{mockTestId}")
    public ResponseEntity<ApiResponse<MockTestDTO>> getMockTest(
            @PathVariable String mockTestId,
            @AuthenticationPrincipal User user) {
        MockTestDTO mockTest = mockTestService.getMockTest(mockTestId, user);
        return ResponseEntity.ok(ApiResponse.success("Mock test retrieved successfully", mockTest));
    }

    /**
     * Get all mock tests for current user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MockTestDTO>>> getUserMockTests(
            @AuthenticationPrincipal User user) {
        List<MockTestDTO> mockTests = mockTestService.getUserMockTests(user);
        return ResponseEntity.ok(ApiResponse.success("Mock tests retrieved successfully", mockTests));
    }

    /**
     * Get paginated mock tests for current user
     */
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<MockTestDTO>>> getUserMockTestsPaginated(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MockTestDTO> mockTests = mockTestService.getUserMockTests(
                user, PageRequest.of(page, size, Sort.by("startedAt").descending()));
        return ResponseEntity.ok(ApiResponse.success("Mock tests retrieved successfully", mockTests));
    }

    /**
     * Get in-progress mock tests
     */
    @GetMapping("/in-progress")
    public ResponseEntity<ApiResponse<List<MockTestDTO>>> getInProgressMockTests(
            @AuthenticationPrincipal User user) {
        List<MockTestDTO> mockTests = mockTestService.getInProgressMockTests(user);
        return ResponseEntity.ok(ApiResponse.success("In-progress mock tests retrieved", mockTests));
    }

    /**
     * Submit skill attempt for mock test
     */
    @PostMapping("/{mockTestId}/submit-skill")
    public ResponseEntity<ApiResponse<MockTestDTO>> submitSkillAttempt(
            @PathVariable String mockTestId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MockTestSkillSubmitRequest request) {
        MockTestDTO mockTest = mockTestService.submitSkillAttempt(mockTestId, user, request);
        return ResponseEntity.ok(ApiResponse.success("Skill attempt submitted successfully", mockTest));
    }

    /**
     * Complete the mock test
     */
    @PostMapping("/{mockTestId}/complete")
    public ResponseEntity<ApiResponse<MockTestResultDTO>> completeMockTest(
            @PathVariable String mockTestId,
            @AuthenticationPrincipal User user) {
        MockTestResultDTO result = mockTestService.completeMockTest(mockTestId, user);
        return ResponseEntity.ok(ApiResponse.success("Mock test completed successfully", result));
    }

    /**
     * Get mock test result
     */
    @GetMapping("/{mockTestId}/result")
    public ResponseEntity<ApiResponse<MockTestResultDTO>> getMockTestResult(
            @PathVariable String mockTestId,
            @AuthenticationPrincipal User user) {
        MockTestResultDTO result = mockTestService.getMockTestResult(mockTestId, user);
        return ResponseEntity.ok(ApiResponse.success("Mock test result retrieved", result));
    }

    /**
     * Delete mock test
     */
    @DeleteMapping("/{mockTestId}")
    public ResponseEntity<ApiResponse<Void>> deleteMockTest(
            @PathVariable String mockTestId,
            @AuthenticationPrincipal User user) {
        mockTestService.deleteMockTest(mockTestId, user);
        return ResponseEntity.ok(ApiResponse.success("Mock test deleted successfully", null));
    }

    /**
     * Admin: Grade mock test
     */
    @PostMapping("/{mockTestId}/grade")
    public ResponseEntity<ApiResponse<MockTestResultDTO>> gradeMockTest(
            @PathVariable String mockTestId,
            @RequestParam(required = false) Double listeningBand,
            @RequestParam(required = false) Double readingBand,
            @RequestParam(required = false) Double writingBand,
            @RequestParam(required = false) Double speakingBand) {
        MockTestResultDTO result = mockTestService.gradeMockTest(
                mockTestId, listeningBand, readingBand, writingBand, speakingBand);
        return ResponseEntity.ok(ApiResponse.success("Mock test graded successfully", result));
    }
}
