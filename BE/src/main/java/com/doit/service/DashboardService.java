package com.doit.service;

import com.doit.dto.dashboard.*;
import com.doit.entity.*;
import com.doit.exception.ResourceNotFoundException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final UserAttemptRepository userAttemptRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final StudyPlanItemRepository studyPlanItemRepository;
    private final MockTestRepository mockTestRepository;

    /**
     * Get comprehensive dashboard data for user
     */
    public DashboardDTO getDashboard(User user) {
        StudyPlan activePlan = studyPlanRepository.findByUserAndIsActiveTrue(user).orElse(null);
        
        return DashboardDTO.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .currentOverallBand(calculateCurrentOverallBand(user))
                .targetBand(activePlan != null ? activePlan.getTargetBand() : null)
                .targetDate(activePlan != null ? activePlan.getTargetDate() : null)
                .daysUntilTarget(calculateDaysUntilTarget(activePlan))
                .totalStudyDays(calculateTotalStudyDays(user))
                .currentStreak(calculateCurrentStreak(user))
                .longestStreak(calculateLongestStreak(user))
                .totalPracticeHours(calculateTotalPracticeHours(user))
                .skillProgress(getSkillProgressList(user))
                .totalAttempts(getTotalAttempts(user))
                .attemptsThisWeek(getAttemptsThisWeek(user))
                .attemptsThisMonth(getAttemptsThisMonth(user))
                .recentActivities(getRecentActivities(user, 10))
                .todayTasksTotal(getTodayTasksTotal(activePlan))
                .todayTasksCompleted(getTodayTasksCompleted(activePlan))
                .upcomingTasksCount(getUpcomingTasksCount(activePlan))
                .statistics(getStatistics(user))
                .recommendations(generateRecommendations(user, activePlan))
                .build();
    }

    /**
     * Get skill progress for all 4 IELTS skills
     */
    public List<SkillProgressDTO> getSkillProgress(User user) {
        return getSkillProgressList(user);
    }

    /**
     * Get recent activities
     */
    public List<RecentActivityDTO> getRecentActivities(User user, int limit) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());

        return attempts.stream()
                .map(this::toRecentActivityDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get progress chart data for a skill
     */
    public ProgressChartDTO getProgressChart(User user, Exam.Skill skill, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .filter(a -> a.getExam() != null && a.getExam().getSkill() == skill)
                .filter(a -> a.getStartedAt() != null && a.getStartedAt().isAfter(startDate))
                .collect(Collectors.toList());

        List<ProgressPointDTO> dataPoints = attempts.stream()
                .filter(a -> a.getBandScore() != null)
                .map(a -> ProgressPointDTO.builder()
                        .date(a.getStartedAt().toLocalDate())
                        .bandScore(a.getBandScore())
                        .attemptId(a.getId())
                        .build())
                .collect(Collectors.toList());

        BigDecimal averageBand = dataPoints.isEmpty() ? BigDecimal.ZERO :
                dataPoints.stream()
                        .map(ProgressPointDTO::getBandScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(dataPoints.size()), 1, RoundingMode.HALF_UP);

        BigDecimal highestBand = dataPoints.stream()
                .map(ProgressPointDTO::getBandScore)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal lowestBand = dataPoints.stream()
                .map(ProgressPointDTO::getBandScore)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return ProgressChartDTO.builder()
                .skill(skill.name())
                .dataPoints(dataPoints)
                .averageBand(averageBand)
                .highestBand(highestBand)
                .lowestBand(lowestBand)
                .build();
    }

    // ============ Private helper methods ============

    private BigDecimal calculateCurrentOverallBand(User user) {
        List<MockTest> gradedTests = mockTestRepository.findGradedMockTestsByUser(user);
        if (gradedTests.isEmpty()) {
            // Calculate from individual skill attempts
            Map<Exam.Skill, BigDecimal> skillBands = new HashMap<>();
            for (Exam.Skill skill : Exam.Skill.values()) {
                BigDecimal avgBand = calculateAverageSkillBand(user, skill);
                if (avgBand.compareTo(BigDecimal.ZERO) > 0) {
                    skillBands.put(skill, avgBand);
                }
            }
            if (skillBands.isEmpty()) return null;
            
            return skillBands.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(skillBands.size()), 1, RoundingMode.HALF_UP);
        }
        
        return gradedTests.get(0).getOverallBand();
    }

    private BigDecimal calculateAverageSkillBand(User user, Exam.Skill skill) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .filter(a -> a.getExam() != null && a.getExam().getSkill() == skill)
                .filter(a -> a.getBandScore() != null)
                .limit(5) // Consider last 5 attempts
                .collect(Collectors.toList());

        if (attempts.isEmpty()) return BigDecimal.ZERO;

        return attempts.stream()
                .map(UserAttempt::getBandScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(attempts.size()), 1, RoundingMode.HALF_UP);
    }

    private Integer calculateDaysUntilTarget(StudyPlan plan) {
        if (plan == null || plan.getTargetDate() == null) return null;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), plan.getTargetDate());
        return days > 0 ? (int) days : 0;
    }

    private Integer calculateTotalStudyDays(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user);
        return (int) attempts.stream()
                .filter(a -> a.getStartedAt() != null)
                .map(a -> a.getStartedAt().toLocalDate())
                .distinct()
                .count();
    }

    private Integer calculateCurrentStreak(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user);
        Set<LocalDate> studyDates = attempts.stream()
                .filter(a -> a.getStartedAt() != null)
                .map(a -> a.getStartedAt().toLocalDate())
                .collect(Collectors.toSet());

        int streak = 0;
        LocalDate checkDate = LocalDate.now();
        
        while (studyDates.contains(checkDate) || studyDates.contains(checkDate.minusDays(1))) {
            if (studyDates.contains(checkDate)) {
                streak++;
            }
            checkDate = checkDate.minusDays(1);
            if (streak > 365) break; // Safety limit
        }
        
        return streak;
    }

    private Integer calculateLongestStreak(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user);
        List<LocalDate> studyDates = attempts.stream()
                .filter(a -> a.getStartedAt() != null)
                .map(a -> a.getStartedAt().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (studyDates.isEmpty()) return 0;

        int maxStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < studyDates.size(); i++) {
            if (studyDates.get(i).minusDays(1).equals(studyDates.get(i - 1))) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        return maxStreak;
    }

    private Integer calculateTotalPracticeHours(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user);
        int totalMinutes = attempts.stream()
                .filter(a -> a.getTimeSpentSeconds() != null)
                .mapToInt(a -> a.getTimeSpentSeconds() / 60)
                .sum();
        return totalMinutes / 60;
    }

    private List<SkillProgressDTO> getSkillProgressList(User user) {
        List<SkillProgressDTO> skillProgress = new ArrayList<>();
        
        for (Exam.Skill skill : Exam.Skill.values()) {
            List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user)
                    .stream()
                    .filter(a -> a.getExam() != null && a.getExam().getSkill() == skill)
                    .collect(Collectors.toList());

            BigDecimal currentBand = calculateAverageSkillBand(user, skill);
            BigDecimal previousBand = calculatePreviousSkillBand(user, skill);
            
            int totalCorrect = attempts.stream()
                    .filter(a -> a.getCorrectAnswers() != null)
                    .mapToInt(UserAttempt::getCorrectAnswers)
                    .sum();
            int totalQuestions = attempts.stream()
                    .filter(a -> a.getTotalQuestions() != null)
                    .mapToInt(UserAttempt::getTotalQuestions)
                    .sum();

            double accuracy = totalQuestions > 0 ? (totalCorrect * 100.0) / totalQuestions : 0;

            int practiceMinutes = attempts.stream()
                    .filter(a -> a.getTimeSpentSeconds() != null)
                    .mapToInt(a -> a.getTimeSpentSeconds() / 60)
                    .sum();

            String trend = determineTrend(currentBand, previousBand);

            skillProgress.add(SkillProgressDTO.builder()
                    .skill(skill.name())
                    .currentBand(currentBand)
                    .previousBand(previousBand)
                    .improvement(currentBand.subtract(previousBand))
                    .totalAttempts(attempts.size())
                    .correctAnswers(totalCorrect)
                    .totalQuestions(totalQuestions)
                    .accuracyPercent(accuracy)
                    .practiceHours(practiceMinutes / 60)
                    .trend(trend)
                    .build());
        }

        return skillProgress;
    }

    private BigDecimal calculatePreviousSkillBand(User user, Exam.Skill skill) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .filter(a -> a.getExam() != null && a.getExam().getSkill() == skill)
                .filter(a -> a.getBandScore() != null)
                .skip(5)
                .limit(5)
                .collect(Collectors.toList());

        if (attempts.isEmpty()) return BigDecimal.ZERO;

        return attempts.stream()
                .map(UserAttempt::getBandScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(attempts.size()), 1, RoundingMode.HALF_UP);
    }

    private String determineTrend(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) return "STABLE";
        int comparison = current.compareTo(previous);
        if (comparison > 0) return "IMPROVING";
        if (comparison < 0) return "DECLINING";
        return "STABLE";
    }

    private Integer getTotalAttempts(User user) {
        return (int) userAttemptRepository.findByUserOrderByStartedAtDesc(user).size();
    }

    private Integer getAttemptsThisWeek(User user) {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        return (int) userAttemptRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .filter(a -> a.getStartedAt() != null && a.getStartedAt().isAfter(weekStart))
                .count();
    }

    private Integer getAttemptsThisMonth(User user) {
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
        return (int) userAttemptRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .filter(a -> a.getStartedAt() != null && a.getStartedAt().isAfter(monthStart))
                .count();
    }

    private RecentActivityDTO toRecentActivityDTO(UserAttempt attempt) {
        return RecentActivityDTO.builder()
                .id(attempt.getId())
                .activityType(attempt.getExam() != null ? attempt.getExam().getSkill().name() : "PRACTICE")
                .title(attempt.getExam() != null ? attempt.getExam().getTitle() : "Practice Session")
                .description("Practice attempt")
                .bandScore(attempt.getBandScore())
                .status(attempt.getStatus().name())
                .timestamp(attempt.getStartedAt())
                .timeSpentMinutes(attempt.getTimeSpentSeconds() != null ? attempt.getTimeSpentSeconds() / 60 : null)
                .build();
    }

    private Integer getTodayTasksTotal(StudyPlan plan) {
        if (plan == null) return 0;
        return (int) studyPlanItemRepository.findByPlanAndRecommendedDate(plan, LocalDate.now()).size();
    }

    private Integer getTodayTasksCompleted(StudyPlan plan) {
        if (plan == null) return 0;
        return (int) studyPlanItemRepository.findByPlanAndRecommendedDate(plan, LocalDate.now())
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsCompleted()))
                .count();
    }

    private Integer getUpcomingTasksCount(StudyPlan plan) {
        if (plan == null) return 0;
        return (int) studyPlanItemRepository.findByPlanAndRecommendedDateBetween(
                plan, LocalDate.now(), LocalDate.now().plusDays(7)).size();
    }

    private Map<String, Object> getStatistics(User user) {
        Map<String, Object> stats = new HashMap<>();
        
        List<MockTest> mockTests = mockTestRepository.findByUserOrderByStartedAtDesc(user);
        stats.put("totalMockTests", mockTests.size());
        stats.put("completedMockTests", mockTests.stream()
                .filter(mt -> mt.getStatus() == MockTest.MockTestStatus.GRADED)
                .count());
        
        BigDecimal avgMockTestBand = mockTests.stream()
                .filter(mt -> mt.getOverallBand() != null)
                .map(MockTest::getOverallBand)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!mockTests.isEmpty()) {
            avgMockTestBand = avgMockTestBand.divide(BigDecimal.valueOf(mockTests.size()), 1, RoundingMode.HALF_UP);
        }
        stats.put("averageMockTestBand", avgMockTestBand);
        
        return stats;
    }

    private List<String> generateRecommendations(User user, StudyPlan plan) {
        List<String> recommendations = new ArrayList<>();
        
        // Check weakest skill
        List<SkillProgressDTO> progress = getSkillProgressList(user);
        SkillProgressDTO weakest = progress.stream()
                .filter(p -> p.getCurrentBand() != null && p.getCurrentBand().compareTo(BigDecimal.ZERO) > 0)
                .min(Comparator.comparing(SkillProgressDTO::getCurrentBand))
                .orElse(null);
        
        if (weakest != null) {
            recommendations.add("Focus on " + weakest.getSkill() + " - it's currently your weakest skill (Band " + weakest.getCurrentBand() + ")");
        }
        
        // Check study streak
        int streak = calculateCurrentStreak(user);
        if (streak == 0) {
            recommendations.add("Start your study streak! Practice daily to build momentum");
        } else if (streak >= 7) {
            recommendations.add("Great job maintaining a " + streak + "-day streak! Keep it up!");
        }
        
        // Check if target is approaching
        if (plan != null && plan.getTargetDate() != null) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), plan.getTargetDate());
            if (daysLeft <= 30 && daysLeft > 0) {
                recommendations.add("Your target date is in " + daysLeft + " days - consider taking a mock test to assess readiness");
            }
        }
        
        // General recommendations
        if (getAttemptsThisWeek(user) < 5) {
            recommendations.add("Try to complete at least 5 practice sessions this week");
        }
        
        return recommendations;
    }
}
