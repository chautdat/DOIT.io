package com.doit.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {

    private long totalUsers;
    private long activeUsers;
    private long newUsersThisMonth;
    
    private long totalExams;
    private long activeExams;
    
    private long totalAttempts;
    private long attemptsThisWeek;
    private long attemptsThisMonth;
    
    private long totalMockTests;
    private long gradedMockTests;
    
    private Double averageOverallBand;
    private Map<String, Double> averageSkillBands;
    
    private Map<String, Long> skillDistribution;
    private Map<String, Long> bandDistribution;
}
