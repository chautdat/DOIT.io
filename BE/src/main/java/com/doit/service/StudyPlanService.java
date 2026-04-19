package com.doit.service;

import com.doit.dto.studyplan.StudyPlanCreateRequest;
import com.doit.dto.studyplan.StudyPlanDTO;
import com.doit.dto.studyplan.StudyPlanItemDTO;
import com.doit.dto.studyplan.StudyPlanUpdateRequest;
import com.doit.entity.Exam;
import com.doit.entity.StudyPlan;
import com.doit.entity.StudyPlanItem;
import com.doit.entity.User;
import com.doit.exception.ResourceNotFoundException;
import com.doit.repository.ExamRepository;
import com.doit.repository.StudyPlanItemRepository;
import com.doit.repository.StudyPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyPlanItemRepository studyPlanItemRepository;
    private final ExamRepository examRepository;

    /**
     * Create a new study plan for user
     */
    public StudyPlanDTO createStudyPlan(User user, StudyPlanCreateRequest request) {
        // Deactivate existing active plans
        studyPlanRepository.findByUserAndIsActiveTrue(user)
                .ifPresent(existingPlan -> {
                    existingPlan.setIsActive(false);
                    studyPlanRepository.save(existingPlan);
                });

        StudyPlan plan = StudyPlan.builder()
                .user(user)
                .currentBand(request.getCurrentBand())
                .targetBand(request.getTargetBand())
                .targetDate(request.getTargetDate())
                .focusListening(request.getFocusListening())
                .focusReading(request.getFocusReading())
                .focusWriting(request.getFocusWriting())
                .focusSpeaking(request.getFocusSpeaking())
                .studyHoursPerDay(request.getStudyHoursPerDay())
                .isActive(true)
                .build();

        StudyPlan saved = studyPlanRepository.save(plan);

        // Generate study plan items
        generateStudyPlanItems(saved);

        log.info("Created study plan {} for user {}", saved.getId(), user.getId());
        return toDTO(saved);
    }

    /**
     * Get active study plan for user
     */
    @Transactional(readOnly = true)
    public StudyPlanDTO getActiveStudyPlan(User user) {
        StudyPlan plan = studyPlanRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));
        return toDTO(plan);
    }

    /**
     * Get study plan by ID
     */
    @Transactional(readOnly = true)
    public StudyPlanDTO getStudyPlan(Long planId, User user) {
        StudyPlan plan = studyPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));
        return toDTO(plan);
    }

    /**
     * Get all study plans for user
     */
    @Transactional(readOnly = true)
    public List<StudyPlanDTO> getAllStudyPlans(User user) {
        return studyPlanRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update study plan
     */
    public StudyPlanDTO updateStudyPlan(Long planId, User user, StudyPlanUpdateRequest request) {
        StudyPlan plan = studyPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));

        if (request.getTargetBand() != null) plan.setTargetBand(request.getTargetBand());
        if (request.getTargetDate() != null) plan.setTargetDate(request.getTargetDate());
        if (request.getFocusListening() != null) plan.setFocusListening(request.getFocusListening());
        if (request.getFocusReading() != null) plan.setFocusReading(request.getFocusReading());
        if (request.getFocusWriting() != null) plan.setFocusWriting(request.getFocusWriting());
        if (request.getFocusSpeaking() != null) plan.setFocusSpeaking(request.getFocusSpeaking());
        if (request.getStudyHoursPerDay() != null) plan.setStudyHoursPerDay(request.getStudyHoursPerDay());

        StudyPlan saved = studyPlanRepository.save(plan);
        log.info("Updated study plan {}", planId);

        return toDTO(saved);
    }

    /**
     * Get today's study items
     */
    @Transactional(readOnly = true)
    public List<StudyPlanItemDTO> getTodayItems(User user) {
        StudyPlan plan = studyPlanRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));

        return studyPlanItemRepository.findByPlanAndRecommendedDate(plan, LocalDate.now())
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming study items
     */
    @Transactional(readOnly = true)
    public List<StudyPlanItemDTO> getUpcomingItems(User user, int days) {
        StudyPlan plan = studyPlanRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);

        return studyPlanItemRepository.findByPlanAndRecommendedDateBetween(plan, startDate, endDate)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending study items
     */
    @Transactional(readOnly = true)
    public List<StudyPlanItemDTO> getPendingItems(User user) {
        StudyPlan plan = studyPlanRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));

        return studyPlanItemRepository.findByPlanAndIsCompletedFalseOrderByRecommendedDate(plan)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark study item as completed
     */
    public StudyPlanItemDTO completeItem(Long itemId, User user) {
        StudyPlanItem item = studyPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan item not found"));

        // Verify ownership
        if (!item.getPlan().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Item does not belong to user");
        }

        item.setIsCompleted(true);
        item.setCompletedDate(LocalDate.now());

        StudyPlanItem saved = studyPlanItemRepository.save(item);
        log.info("Completed study item {} for user {}", itemId, user.getId());

        return toItemDTO(saved);
    }

    /**
     * Regenerate study plan items
     */
    public StudyPlanDTO regeneratePlan(Long planId, User user) {
        StudyPlan plan = studyPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));

        // Remove existing incomplete items
        List<StudyPlanItem> items = studyPlanItemRepository.findByPlan(plan);
        for (StudyPlanItem item : items) {
            if (!item.getIsCompleted()) {
                studyPlanItemRepository.delete(item);
            }
        }

        // Generate new items
        generateStudyPlanItems(plan);

        log.info("Regenerated study plan {}", planId);
        return toDTO(plan);
    }

    /**
     * Delete study plan
     */
    public void deleteStudyPlan(Long planId, User user) {
        StudyPlan plan = studyPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));

        studyPlanRepository.delete(plan);
        log.info("Deleted study plan {} for user {}", planId, user.getId());
    }

    /**
     * Generate study plan items based on settings
     */
    private void generateStudyPlanItems(StudyPlan plan) {
        List<Exam.Skill> focusSkills = new ArrayList<>();
        if (Boolean.TRUE.equals(plan.getFocusListening())) focusSkills.add(Exam.Skill.LISTENING);
        if (Boolean.TRUE.equals(plan.getFocusReading())) focusSkills.add(Exam.Skill.READING);
        if (Boolean.TRUE.equals(plan.getFocusWriting())) focusSkills.add(Exam.Skill.WRITING);
        if (Boolean.TRUE.equals(plan.getFocusSpeaking())) focusSkills.add(Exam.Skill.SPEAKING);

        if (focusSkills.isEmpty()) {
            focusSkills = Arrays.asList(Exam.Skill.values());
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = plan.getTargetDate() != null ? plan.getTargetDate() : startDate.plusMonths(3);
        
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) totalDays = 90; // Default 3 months

        int orderNumber = 1;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            // Distribute skills across days
            for (Exam.Skill skill : focusSkills) {
                List<Exam> exams = examRepository.findBySkillAndIsActiveTrue(skill);
                
                // Create practice activity
                StudyPlanItem practiceItem = StudyPlanItem.builder()
                        .plan(plan)
                        .skill(skill)
                        .activityType("PRACTICE")
                        .activityDescription(generatePracticeDescription(skill))
                        .recommendedDate(currentDate)
                        .orderNumber(orderNumber++)
                        .isCompleted(false)
                        .build();

                if (exams != null && !exams.isEmpty()) {
                    practiceItem.setExam(exams.get(new Random().nextInt(exams.size())));
                }

                studyPlanItemRepository.save(practiceItem);
            }

            // Add weekly review
            if (currentDate.getDayOfWeek().getValue() == 7) {
                StudyPlanItem reviewItem = StudyPlanItem.builder()
                        .plan(plan)
                        .skill(focusSkills.get(0))
                        .activityType("REVIEW")
                        .activityDescription("Weekly review and progress assessment")
                        .recommendedDate(currentDate)
                        .orderNumber(orderNumber++)
                        .isCompleted(false)
                        .build();
                studyPlanItemRepository.save(reviewItem);
            }

            // Add monthly mock test
            if (currentDate.getDayOfMonth() == 1 && !currentDate.equals(startDate)) {
                StudyPlanItem mockTestItem = StudyPlanItem.builder()
                        .plan(plan)
                        .skill(Exam.Skill.LISTENING) // Primary skill for mock test
                        .activityType("MOCK_TEST")
                        .activityDescription("Full IELTS Mock Test - All 4 skills")
                        .recommendedDate(currentDate)
                        .orderNumber(orderNumber++)
                        .isCompleted(false)
                        .build();
                studyPlanItemRepository.save(mockTestItem);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    /**
     * Generate practice description based on skill
     */
    private String generatePracticeDescription(Exam.Skill skill) {
        return switch (skill) {
            case LISTENING -> "Practice listening comprehension with various accents and question types";
            case READING -> "Practice reading passages with focus on skimming, scanning, and detailed reading";
            case WRITING -> "Practice Task 1 and Task 2 essays with focus on structure and coherence";
            case SPEAKING -> "Practice speaking with focus on fluency, vocabulary, and pronunciation";
        };
    }

    /**
     * Convert StudyPlan to DTO
     */
    private StudyPlanDTO toDTO(StudyPlan plan) {
        Long totalItems = studyPlanItemRepository.countByPlan(plan);
        Long completedItems = studyPlanItemRepository.countByPlanAndIsCompletedTrue(plan);
        int progressPercent = totalItems > 0 ? (int) ((completedItems * 100) / totalItems) : 0;

        List<StudyPlanItemDTO> itemDTOs = plan.getItems() != null ?
                plan.getItems().stream()
                        .map(this::toItemDTO)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        return StudyPlanDTO.builder()
                .id(plan.getId())
                .userId(plan.getUser().getId())
                .currentBand(plan.getCurrentBand())
                .targetBand(plan.getTargetBand())
                .targetDate(plan.getTargetDate())
                .focusListening(plan.getFocusListening())
                .focusReading(plan.getFocusReading())
                .focusWriting(plan.getFocusWriting())
                .focusSpeaking(plan.getFocusSpeaking())
                .studyHoursPerDay(plan.getStudyHoursPerDay())
                .isActive(plan.getIsActive())
                .items(itemDTOs)
                .totalItems(totalItems.intValue())
                .completedItems(completedItems.intValue())
                .progressPercent(progressPercent)
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    /**
     * Convert StudyPlanItem to DTO
     */
    private StudyPlanItemDTO toItemDTO(StudyPlanItem item) {
        return StudyPlanItemDTO.builder()
                .id(item.getId())
                .skill(item.getSkill())
                .examId(item.getExam() != null ? item.getExam().getId() : null)
                .activityType(item.getActivityType())
                .activityDescription(item.getActivityDescription())
                .recommendedDate(item.getRecommendedDate())
                .isCompleted(item.getIsCompleted())
                .completedDate(item.getCompletedDate())
                .orderNumber(item.getOrderNumber())
                .build();
    }
}
