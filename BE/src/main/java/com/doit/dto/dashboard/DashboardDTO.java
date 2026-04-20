package com.doit.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    // User overview
    private String userId;
    private String userName;
    private Double currentOverallBand;
    private Double targetBand;
    private LocalDate targetDate;
    private Integer daysUntilTarget;

    // Study progress
    private Integer totalStudyDays;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalPracticeHours;

    // Skill progress
    private List<SkillProgressDTO> skillProgress;

    // Activity summary
    private Integer totalAttempts;
    private Integer attemptsThisWeek;
    private Integer attemptsThisMonth;

    // Recent activity
    private List<RecentActivityDTO> recentActivities;

    // Today's tasks
    private Integer todayTasksTotal;
    private Integer todayTasksCompleted;

    // Upcoming tasks
    private Integer upcomingTasksCount;

    // Statistics
    private StatisticsDTO statistics;

    // Recommendations
    private List<String> recommendations;
}
