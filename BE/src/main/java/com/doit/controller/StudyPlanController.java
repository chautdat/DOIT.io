package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.studyplan.*;
import com.doit.entity.User;
import com.doit.service.StudyPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/study-plan")
@RequiredArgsConstructor
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudyPlanDTO>> createStudyPlan(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudyPlanCreateRequest request) {
        StudyPlanDTO plan = studyPlanService.createStudyPlan(user, request);
        return ResponseEntity.ok(ApiResponse.success("Study plan created successfully", plan));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<StudyPlanDTO>> getActiveStudyPlan(@AuthenticationPrincipal User user) {
        StudyPlanDTO plan = studyPlanService.getActiveStudyPlan(user);
        return ResponseEntity.ok(ApiResponse.success("Active study plan retrieved", plan));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<StudyPlanDTO>> getStudyPlan(
            @PathVariable String planId,
            @AuthenticationPrincipal User user) {
        StudyPlanDTO plan = studyPlanService.getStudyPlan(planId, user);
        return ResponseEntity.ok(ApiResponse.success("Study plan retrieved", plan));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudyPlanDTO>>> getAllStudyPlans(@AuthenticationPrincipal User user) {
        List<StudyPlanDTO> plans = studyPlanService.getAllStudyPlans(user);
        return ResponseEntity.ok(ApiResponse.success("Study plans retrieved", plans));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<ApiResponse<StudyPlanDTO>> updateStudyPlan(
            @PathVariable String planId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudyPlanUpdateRequest request) {
        StudyPlanDTO plan = studyPlanService.updateStudyPlan(planId, user, request);
        return ResponseEntity.ok(ApiResponse.success("Study plan updated", plan));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<StudyPlanItemDTO>>> getTodayItems(@AuthenticationPrincipal User user) {
        List<StudyPlanItemDTO> items = studyPlanService.getTodayItems(user);
        return ResponseEntity.ok(ApiResponse.success("Today's study items retrieved", items));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<StudyPlanItemDTO>>> getUpcomingItems(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "7") int days) {
        List<StudyPlanItemDTO> items = studyPlanService.getUpcomingItems(user, days);
        return ResponseEntity.ok(ApiResponse.success("Upcoming study items retrieved", items));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<StudyPlanItemDTO>>> getPendingItems(@AuthenticationPrincipal User user) {
        List<StudyPlanItemDTO> items = studyPlanService.getPendingItems(user);
        return ResponseEntity.ok(ApiResponse.success("Pending study items retrieved", items));
    }

    @PostMapping("/item/{itemId}/complete")
    public ResponseEntity<ApiResponse<StudyPlanItemDTO>> completeItem(
            @PathVariable String itemId,
            @AuthenticationPrincipal User user) {
        StudyPlanItemDTO item = studyPlanService.completeItem(itemId, user);
        return ResponseEntity.ok(ApiResponse.success("Study item marked as completed", item));
    }

    @PostMapping("/{planId}/regenerate")
    public ResponseEntity<ApiResponse<StudyPlanDTO>> regeneratePlan(
            @PathVariable String planId,
            @AuthenticationPrincipal User user) {
        StudyPlanDTO plan = studyPlanService.regeneratePlan(planId, user);
        return ResponseEntity.ok(ApiResponse.success("Study plan regenerated", plan));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudyPlan(
            @PathVariable String planId,
            @AuthenticationPrincipal User user) {
        studyPlanService.deleteStudyPlan(planId, user);
        return ResponseEntity.ok(ApiResponse.success("Study plan deleted", null));
    }
}
