package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.placement.*;
import com.doit.service.PlacementTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        PlacementTestDTO info = placementTestService.getPlacementTestInfo(userId);
        return ResponseEntity.ok(ApiResponse.success("Placement test info retrieved", info));
    }

    /**
     * Start placement test
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<Long>> startPlacementTest(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        Long attemptId = placementTestService.startPlacementTest(userId);
        return ResponseEntity.ok(ApiResponse.success("Placement test started", attemptId));
    }

    /**
     * Submit placement test
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<PlacementResultDTO>> submitPlacementTest(
            @Valid @RequestBody PlacementSubmitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        PlacementResultDTO result = placementTestService.submitPlacementTest(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Placement test completed", result));
    }

    /**
     * Get placement test result
     */
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<PlacementResultDTO>> getPlacementResult(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserId(userDetails);
        PlacementResultDTO result = placementTestService.getPlacementResult(userId);
        return ResponseEntity.ok(ApiResponse.success("Placement result retrieved", result));
    }

    private Long extractUserId(UserDetails userDetails) {
        if (userDetails instanceof com.doit.entity.User) {
            return ((com.doit.entity.User) userDetails).getId();
        }
        throw new IllegalStateException("Unable to extract user ID");
    }
}
