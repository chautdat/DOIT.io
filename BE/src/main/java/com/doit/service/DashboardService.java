package com.doit.service;

import com.doit.dto.dashboard.*;
import com.doit.entity.*;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserAttemptRepository userAttemptRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final StudyPlanItemRepository studyPlanItemRepository;
    private final MockTestRepository mockTestRepository;
    private final ExamRepository examRepository;

    public DashboardDTO getDashboard(User user) {
        StudyPlan activePlan = studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId()).orElse(null);
        
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

    public List<SkillProgressDTO> getSkillProgress(User user) {
        return getSkillProgressList(user);
    }

    public List<RecentActivityDTO> getRecentActivities(User user, int limit) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId())
                .stream().limit(limit).collect(Collectors.toList());
        return attempts.stream().map(this::toRecentActivityDTO).collect(Collectors.toList());
    }

    public ProgressChartDTO getProgressChart(User user, String skill, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdAndSkillOrderByStartedAtDesc(user.getId(), skill)
                .stream()
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

        Double averageBand = dataPoints.isEmpty() ? 0.0 :
                dataPoints.stream().mapToDouble(ProgressPointDTO::getBandScore).average().orElse(0.0);
        Double highestBand = dataPoints.stream().mapToDouble(ProgressPointDTO::getBandScore).max().orElse(0.0);
        Double lowestBand = dataPoints.stream().mapToDouble(ProgressPointDTO::getBandScore).min().orElse(0.0);

        return ProgressChartDTO.builder()
                .skill(skill)
                .dataPoints(dataPoints)
                .averageBand(averageBand)
                .highestBand(highestBand)
                .lowestBand(lowestBand)
                .build();
    }

    private Double calculateCurrentOverallBand(User user) {
        List<MockTest> gradedTests = mockTestRepository.findCompletedMockTestsByUserId(user.getId());
        if (!gradedTests.isEmpty()) {
            return gradedTests.get(0).getOverallBand();
        }
        Map<String, Double> skillBands = new HashMap<>();
        for (String skill : Arrays.asList("LISTENING", "READING", "WRITING", "SPEAKING")) {
            Double avgBand = calculateAverageSkillBand(user, skill);
            if (avgBand > 0) skillBands.put(skill, avgBand);
        }
        if (skillBands.isEmpty()) return null;
        return skillBands.values().stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    private Double calculateAverageSkillBand(User user, String skill) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdAndSkillOrderByStartedAtDesc(user.getId(), skill)
                .stream().filter(a -> a.getBandScore() != null).limit(5).collect(Collectors.toList());
        if (attempts.isEmpty()) return 0.0;
        return attempts.stream().mapToDouble(UserAttempt::getBandScore).average().orElse(0.0);
    }

    private Integer calculateDaysUntilTarget(StudyPlan plan) {
        if (plan == null || plan.getTargetDate() == null) return null;
        long days = ChronoUnit.DAYS.between(LocalDate.now(), plan.getTargetDate());
        return days > 0 ? (int) days : 0;
    }

    private Integer calculateTotalStudyDays(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId());
        return (int) attempts.stream()
                .filter(a -> a.getStartedAt() != null)
                .map(a -> a.getStartedAt().toLocalDate()).distinct().count();
    }

    private Integer calculateCurrentStreak(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId());
        Set<LocalDate> studyDates = attempts.stream()
                .filter(a -> a.getStartedAt() != null)
                .map(a -> a.getStartedAt().toLocalDate()).collect(Collectors.toSet());
        int streak = 0;
        LocalDate checkDate = LocalDate.now();
        while (studyDates.contains(checkDate) || studyDates.contains(checkDate.minusDays(1))) {
            if (studyDates.contains(checkDate)) streak++;
            checkDate = checkDate.minusDays(1);
            if (streak > 365) break;
        }
        return streak;
    }

    private Integer calculateLongestStreak(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId());
        List<LocalDate> studyDates = attempts.stream()
                .filter(a -> a.getStartedAt() != null)
                .map(a -> a.getStartedAt().toLocalDate()).distinct().sorted().collect(Collectors.toList());
        if (studyDates.isEmpty()) return 0;
        int maxStreak = 1, currentStreak = 1;
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
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId());
        int totalMinutes = attempts.stream()
                .filter(a -> a.getTimeSpentSeconds() != null)
                .mapToInt(a -> a.getTimeSpentSeconds() / 60).sum();
        return totalMinutes / 60;
    }

    private List<SkillProgressDTO> getSkillProgressList(User user) {
        List<SkillProgressDTO> skillProgress = new ArrayList<>();
        for (String skill : Arrays.asList("LISTENING", "READING", "WRITING", "SPEAKING")) {
            List<UserAttempt> attempts = userAttemptRepository.findByUserIdAndSkillOrderByStartedAtDesc(user.getId(), skill);
            Double currentBand = calculateAverageSkillBand(user, skill);
            Double previousBand = calculatePreviousSkillBand(user, skill);
            int totalCorrect = attempts.stream().filter(a -> a.getCorrectAnswers() != null).mapToInt(UserAttempt::getCorrectAnswers).sum();
            int totalQuestions = attempts.stream().filter(a -> a.getTotalQuestions() != null).mapToInt(UserAttempt::getTotalQuestions).sum();
            double accuracy = totalQuestions > 0 ? (totalCorrect * 100.0) / totalQuestions : 0;
            int practiceMinutes = attempts.stream().filter(a -> a.getTimeSpentSeconds() != null).mapToInt(a -> a.getTimeSpentSeconds() / 60).sum();
            String trend = determineTrend(currentBand, previousBand);

            skillProgress.add(SkillProgressDTO.builder()
                    .skill(skill).currentBand(currentBand).previousBand(previousBand)
                    .improvement(currentBand - previousBand).totalAttempts(attempts.size())
                    .correctAnswers(totalCorrect).totalQuestions(totalQuestions)
                    .accuracyPercent(accuracy).practiceHours(practiceMinutes / 60).trend(trend).build());
        }
        return skillProgress;
    }

    private Double calculatePreviousSkillBand(User user, String skill) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdAndSkillOrderByStartedAtDesc(user.getId(), skill)
                .stream().filter(a -> a.getBandScore() != null).skip(5).limit(5).collect(Collectors.toList());
        if (attempts.isEmpty()) return 0.0;
        return attempts.stream().mapToDouble(UserAttempt::getBandScore).average().orElse(0.0);
    }

    private String determineTrend(Double current, Double previous) {
        if (current > previous) return "UP";
        if (current < previous) return "DOWN";
        return "STABLE";
    }

    private Integer getTotalAttempts(User user) {
        return userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId()).size();
    }

    private Integer getAttemptsThisWeek(User user) {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        return (int) userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId()).stream()
                .filter(a -> a.getStartedAt() != null && a.getStartedAt().isAfter(weekStart)).count();
    }

    private Integer getAttemptsThisMonth(User user) {
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
        return (int) userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId()).stream()
                .filter(a -> a.getStartedAt() != null && a.getStartedAt().isAfter(monthStart)).count();
    }

    private Integer getTodayTasksTotal(StudyPlan plan) {
        if (plan == null) return 0;
        return studyPlanItemRepository.findByStudyPlanIdAndScheduledDate(plan.getId(), LocalDate.now()).size();
    }

    private Integer getTodayTasksCompleted(StudyPlan plan) {
        if (plan == null) return 0;
        return (int) studyPlanItemRepository.findByStudyPlanIdAndScheduledDate(plan.getId(), LocalDate.now()).stream()
                .filter(StudyPlanItem::getIsCompleted).count();
    }

    private Integer getUpcomingTasksCount(StudyPlan plan) {
        if (plan == null) return 0;
        return studyPlanItemRepository.findByStudyPlanIdAndIsCompletedFalseOrderByScheduledDate(plan.getId()).size();
    }

    private StatisticsDTO getStatistics(User user) {
        List<UserAttempt> attempts = userAttemptRepository.findByUserIdOrderByStartedAtDesc(user.getId());
        Double avgScore = attempts.stream().filter(a -> a.getBandScore() != null).mapToDouble(UserAttempt::getBandScore).average().orElse(0.0);
        Double highestScore = attempts.stream().filter(a -> a.getBandScore() != null).mapToDouble(UserAttempt::getBandScore).max().orElse(0.0);
        return StatisticsDTO.builder().averageScore(avgScore).highestScore(highestScore)
                .totalAttempts(attempts.size()).completedTests(mockTestRepository.countByUserId(user.getId())).build();
    }

    private List<String> generateRecommendations(User user, StudyPlan plan) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Practice consistently to improve your band score");
        if (plan == null) recommendations.add("Create a study plan to track your progress");
        return recommendations;
    }

    private RecentActivityDTO toRecentActivityDTO(UserAttempt attempt) {
        Exam exam = examRepository.findById(attempt.getExamId()).orElse(null);
        return RecentActivityDTO.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExamId())
                .examTitle(exam != null ? exam.getTitle() : "Unknown")
                .skill(attempt.getSkill())
                .bandScore(attempt.getBandScore())
                .correctAnswers(attempt.getCorrectAnswers())
                .totalQuestions(attempt.getTotalQuestions())
                .startedAt(attempt.getStartedAt())
                .completedAt(attempt.getSubmittedAt())
                .build();
    }
}
