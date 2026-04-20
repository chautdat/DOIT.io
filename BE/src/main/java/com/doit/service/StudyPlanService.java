package com.doit.service;

import com.doit.dto.studyplan.*;
import com.doit.entity.*;
import com.doit.exception.ResourceNotFoundException;
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
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyPlanItemRepository studyPlanItemRepository;
    private final ExamRepository examRepository;

    public StudyPlanDTO createStudyPlan(User user, StudyPlanCreateRequest request) {
        studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
                .ifPresent(existingPlan -> {
                    existingPlan.setIsActive(false);
                    studyPlanRepository.save(existingPlan);
                });

        List<String> focusSkills = new ArrayList<>();
        if (Boolean.TRUE.equals(request.getFocusListening())) focusSkills.add("LISTENING");
        if (Boolean.TRUE.equals(request.getFocusReading())) focusSkills.add("READING");
        if (Boolean.TRUE.equals(request.getFocusWriting())) focusSkills.add("WRITING");
        if (Boolean.TRUE.equals(request.getFocusSpeaking())) focusSkills.add("SPEAKING");

        StudyPlan plan = StudyPlan.builder()
                .userId(user.getId())
                .name(request.getName() != null ? request.getName() : "Study Plan")
                .targetBand(request.getTargetBand())
                .targetDate(request.getTargetDate())
                .focusSkills(focusSkills)
                .isActive(true)
                .build();

        StudyPlan saved = studyPlanRepository.save(plan);
        generateStudyPlanItems(saved);

        log.info("Created study plan {} for user {}", saved.getId(), user.getId());
        return toDTO(saved);
    }

    public StudyPlanDTO getActiveStudyPlan(User user) {
        StudyPlan plan = studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));
        return toDTO(plan);
    }

    public StudyPlanDTO getStudyPlan(String planId, User user) {
        StudyPlan plan = studyPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));
        return toDTO(plan);
    }

    public List<StudyPlanDTO> getAllStudyPlans(User user) {
        return studyPlanRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public StudyPlanDTO updateStudyPlan(String planId, User user, StudyPlanUpdateRequest request) {
        StudyPlan plan = studyPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));

        if (request.getTargetBand() != null) plan.setTargetBand(request.getTargetBand());
        if (request.getTargetDate() != null) plan.setTargetDate(request.getTargetDate());
        if (request.getName() != null) plan.setName(request.getName());

        StudyPlan saved = studyPlanRepository.save(plan);
        log.info("Updated study plan {}", planId);
        return toDTO(saved);
    }

    public List<StudyPlanItemDTO> getTodayItems(User user) {
        StudyPlan plan = studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));
        return studyPlanItemRepository.findByStudyPlanIdAndScheduledDate(plan.getId(), LocalDate.now())
                .stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    public List<StudyPlanItemDTO> getUpcomingItems(User user, int days) {
        StudyPlan plan = studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return studyPlanItemRepository.findByStudyPlanIdAndScheduledDateBetween(plan.getId(), startDate, endDate)
                .stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    public List<StudyPlanItemDTO> getPendingItems(User user) {
        StudyPlan plan = studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active study plan found"));
        return studyPlanItemRepository.findByStudyPlanIdAndIsCompletedFalseOrderByScheduledDate(plan.getId())
                .stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    public StudyPlanItemDTO completeItem(String itemId, User user) {
        StudyPlanItem item = studyPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan item not found"));
        StudyPlan plan = studyPlanRepository.findById(item.getStudyPlanId()).orElse(null);
        if (plan == null || !plan.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("Item does not belong to user");
        }
        item.setIsCompleted(true);
        item.setCompletedAt(LocalDateTime.now());
        StudyPlanItem saved = studyPlanItemRepository.save(item);
        log.info("Completed study item {} for user {}", itemId, user.getId());
        return toItemDTO(saved);
    }

    public StudyPlanDTO regeneratePlan(String planId, User user) {
        StudyPlan plan = studyPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));
        List<StudyPlanItem> items = studyPlanItemRepository.findByStudyPlanId(plan.getId());
        for (StudyPlanItem item : items) {
            if (!Boolean.TRUE.equals(item.getIsCompleted())) {
                studyPlanItemRepository.delete(item);
            }
        }
        generateStudyPlanItems(plan);
        log.info("Regenerated study plan {}", planId);
        return toDTO(plan);
    }

    public void deleteStudyPlan(String planId, User user) {
        StudyPlan plan = studyPlanRepository.findByIdAndUserId(planId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found"));
        studyPlanItemRepository.findByStudyPlanId(plan.getId()).forEach(studyPlanItemRepository::delete);
        studyPlanRepository.delete(plan);
        log.info("Deleted study plan {} for user {}", planId, user.getId());
    }

    private void generateStudyPlanItems(StudyPlan plan) {
        List<String> focusSkills = plan.getFocusSkills();
        if (focusSkills == null || focusSkills.isEmpty()) {
            focusSkills = Arrays.asList("LISTENING", "READING", "WRITING", "SPEAKING");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = plan.getTargetDate() != null ? plan.getTargetDate() : startDate.plusMonths(3);
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) totalDays = 90;

        int orderNumber = 1;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            for (String skill : focusSkills) {
                StudyPlanItem practiceItem = StudyPlanItem.builder()
                        .studyPlanId(plan.getId())
                        .skill(skill)
                        .scheduledDate(currentDate)
                        .orderIndex(orderNumber++)
                        .isCompleted(false)
                        .build();
                List<Exam> exams = examRepository.findBySkillAndIsActiveTrue(Exam.Skill.valueOf(skill));
                if (exams != null && !exams.isEmpty()) {
                    practiceItem.setExamId(exams.get(new Random().nextInt(exams.size())).getId());
                }
                studyPlanItemRepository.save(practiceItem);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    private StudyPlanDTO toDTO(StudyPlan plan) {
        Long totalItems = studyPlanItemRepository.countByStudyPlanId(plan.getId());
        Long completedItems = studyPlanItemRepository.countByStudyPlanIdAndIsCompletedTrue(plan.getId());
        int progressPercent = totalItems > 0 ? (int) ((completedItems * 100) / totalItems) : 0;

        List<StudyPlanItemDTO> itemDTOs = studyPlanItemRepository.findByStudyPlanIdOrderByOrderIndex(plan.getId())
                .stream().map(this::toItemDTO).collect(Collectors.toList());

        return StudyPlanDTO.builder()
                .id(plan.getId())
                .userId(plan.getUserId())
                .name(plan.getName())
                .targetBand(plan.getTargetBand())
                .targetDate(plan.getTargetDate())
                .focusSkills(plan.getFocusSkills())
                .isActive(plan.getIsActive())
                .items(itemDTOs)
                .totalItems(totalItems.intValue())
                .completedItems(completedItems.intValue())
                .progressPercent(progressPercent)
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    private StudyPlanItemDTO toItemDTO(StudyPlanItem item) {
        return StudyPlanItemDTO.builder()
                .id(item.getId())
                .skill(item.getSkill())
                .examId(item.getExamId())
                .scheduledDate(item.getScheduledDate())
                .isCompleted(item.getIsCompleted())
                .completedAt(item.getCompletedAt())
                .orderIndex(item.getOrderIndex())
                .build();
    }
}
