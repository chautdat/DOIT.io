package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.dto.dashboard.*;
import com.doit.entity.Exam;
import com.doit.entity.User;
import com.doit.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get comprehensive dashboard data
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard(
            @AuthenticationPrincipal User user) {
        DashboardDTO dashboard = dashboardService.getDashboard(user);
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
    }

    /**
     * Get skill progress for all skills
     */
    @GetMapping("/skill-progress")
    public ResponseEntity<ApiResponse<List<SkillProgressDTO>>> getSkillProgress(
            @AuthenticationPrincipal User user) {
        List<SkillProgressDTO> progress = dashboardService.getSkillProgress(user);
        return ResponseEntity.ok(ApiResponse.success("Skill progress retrieved", progress));
    }

    /**
     * Get recent activities
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<ApiResponse<List<RecentActivityDTO>>> getRecentActivities(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "10") int limit) {
        List<RecentActivityDTO> activities = dashboardService.getRecentActivities(user, limit);
        return ResponseEntity.ok(ApiResponse.success("Recent activities retrieved", activities));
    }

    /**
     * Get progress chart data for a specific skill
     */
    @GetMapping("/progress-chart/{skill}")
    public ResponseEntity<ApiResponse<ProgressChartDTO>> getProgressChart(
            @PathVariable Exam.Skill skill,
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "30") int days) {
        ProgressChartDTO chart = dashboardService.getProgressChart(user, skill, days);
        return ResponseEntity.ok(ApiResponse.success("Progress chart retrieved", chart));
    }
}
