package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.placement.*;
import com.doit.entity.User;
import com.doit.service.PlacementTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/placement")
@RequiredArgsConstructor
public class PlacementTestController {

    private final PlacementTestService placementTestService;

    /**
     * Get placement test info
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<PlacementTestDTO>> getPlacementTestInfo(
            @AuthenticationPrincipal User user) {
        PlacementTestDTO info = placementTestService.getPlacementTestInfo(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Placement test info retrieved", info));
    }

    /**
     * Start placement test
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startPlacementTest(
            @AuthenticationPrincipal User user) {
        String attemptId = placementTestService.startPlacementTest(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Placement test started", attemptId));
    }

    /**
     * Submit placement test
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<PlacementResultDTO>> submitPlacementTest(
            @Valid @RequestBody PlacementSubmitRequest request,
            @AuthenticationPrincipal User user) {
        PlacementResultDTO result = placementTestService.submitPlacementTest(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Placement test completed", result));
    }

    /**
     * Get placement test result
     */
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<PlacementResultDTO>> getPlacementResult(
            @AuthenticationPrincipal User user) {
        PlacementResultDTO result = placementTestService.getPlacementResult(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Placement result retrieved", result));
    }
}
