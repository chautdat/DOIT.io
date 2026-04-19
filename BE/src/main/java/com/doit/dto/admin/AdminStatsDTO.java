package com.doit.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {

    private Long totalUsers;
    private Long activeUsers;
    private Long newUsersThisMonth;
    
    private Long totalExams;
    private Long activeExams;
    
    private Long totalAttempts;
    private Long attemptsThisWeek;
    private Long attemptsThisMonth;
    
    private Long totalMockTests;
    private Long gradedMockTests;
    
    private BigDecimal averageOverallBand;
    private Map<String, BigDecimal> averageSkillBands;
    
    private Map<String, Long> skillDistribution;
    private Map<String, Long> bandDistribution;
}
