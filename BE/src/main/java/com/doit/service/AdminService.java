package com.doit.service;

import com.doit.dto.admin.*;
import com.doit.entity.*;
import com.doit.exception.BadRequestException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final UserAttemptRepository userAttemptRepository;
    private final MockTestRepository mockTestRepository;
    private final WritingSubmissionRepository writingSubmissionRepository;
    private final SpeakingSubmissionRepository speakingSubmissionRepository;

    public Page<UserAdminDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserAdminDTO);
    }
    
    public UserAdminDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return mapToUserAdminDTO(user);
    }
    
    public UserAdminDTO updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        try {
            User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + request.getRole());
        }
        
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        User savedUser = userRepository.save(user);
        return mapToUserAdminDTO(savedUser);
    }
    
    public UserAdminDTO updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        user.setIsActive(request.getIsActive());
        User savedUser = userRepository.save(user);
        return mapToUserAdminDTO(savedUser);
    }
    
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found");
        }
        userRepository.deleteById(userId);
    }
    
    private UserAdminDTO mapToUserAdminDTO(User user) {
        List<UserAttempt> userAttempts = userAttemptRepository.findByUserOrderByStartedAtDesc(user);
        List<MockTest> userMockTests = mockTestRepository.findByUserOrderByStartedAtDesc(user);
        
        return UserAdminDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getUpdatedAt())
                .totalAttempts(userAttempts.size())
                .totalMockTests(userMockTests.size())
                .build();
    }

    public Page<ExamAdminDTO> getAllExams(Pageable pageable) {
        Page<Exam> exams = examRepository.findAll(pageable);
        return exams.map(this::mapToExamAdminDTO);
    }
    
    public ExamAdminDTO getExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        return mapToExamAdminDTO(exam);
    }
    
    public ExamAdminDTO toggleExamStatus(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        
        exam.setIsActive(!exam.getIsActive());
        Exam savedExam = examRepository.save(exam);
        return mapToExamAdminDTO(savedExam);
    }
    
    private ExamAdminDTO mapToExamAdminDTO(Exam exam) {
        List<UserAttempt> allAttempts = userAttemptRepository.findAll();
        List<UserAttempt> attempts = allAttempts.stream()
                .filter(a -> a.getExam().getId().equals(exam.getId()))
                .collect(Collectors.toList());
        long totalAttempts = attempts.size();
        
        long passCount = attempts.stream()
                .filter(a -> a.getBandScore() != null && a.getBandScore().compareTo(new BigDecimal("5.0")) >= 0)
                .count();
        
        Double avgScore = attempts.stream()
                .filter(a -> a.getBandScore() != null)
                .mapToDouble(a -> a.getBandScore().doubleValue())
                .average()
                .orElse(0.0);
        
        return ExamAdminDTO.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .type(exam.getType())
                .skill(exam.getSkill())
                .bandLevel(exam.getBandLevel())
                .timeLimitMinutes(exam.getDurationMinutes())
                .isActive(exam.getIsActive())
                .createdAt(exam.getCreatedAt())
                .totalAttempts(totalAttempts)
                .passCount(passCount)
                .averageScore(avgScore)
                .build();
    }

    public Page<SubmissionAdminDTO> getWritingSubmissions(Pageable pageable) {
        Page<WritingSubmission> submissions = writingSubmissionRepository.findAll(pageable);
        return submissions.map(this::mapWritingToSubmissionDTO);
    }
    
    public Page<SubmissionAdminDTO> getSpeakingSubmissions(Pageable pageable) {
        Page<SpeakingSubmission> submissions = speakingSubmissionRepository.findAll(pageable);
        return submissions.map(this::mapSpeakingToSubmissionDTO);
    }
    
    public SubmissionAdminDTO gradeWritingSubmission(Long submissionId, GradeSubmissionRequest request) {
        WritingSubmission submission = writingSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new BadRequestException("Writing submission not found"));
        
        submission.setBandScore(request.getBandScore());
        submission.setAiFeedback(request.getFeedback());
        submission.setTaskResponseScore(request.getBandScore());
        submission.setGradedAt(LocalDateTime.now());
        
        WritingSubmission saved = writingSubmissionRepository.save(submission);
        return mapWritingToSubmissionDTO(saved);
    }
    
    public SubmissionAdminDTO gradeSpeakingSubmission(Long submissionId, GradeSubmissionRequest request) {
        SpeakingSubmission submission = speakingSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new BadRequestException("Speaking submission not found"));
        
        submission.setBandScore(request.getBandScore());
        submission.setAiFeedback(request.getFeedback());
        submission.setFluencyCoherenceScore(request.getBandScore());
        submission.setGradedAt(LocalDateTime.now());
        
        SpeakingSubmission saved = speakingSubmissionRepository.save(submission);
        return mapSpeakingToSubmissionDTO(saved);
    }
    
    private SubmissionAdminDTO mapWritingToSubmissionDTO(WritingSubmission submission) {
        User user = submission.getAttempt().getUser();
        WritingTask task = submission.getTask();
        
        return SubmissionAdminDTO.builder()
                .id(submission.getId())
                .type("WRITING")
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userFullName(user.getFullName())
                .taskId(task.getId())
                .taskTitle("Task " + task.getTaskNumber())
                .submissionContent(submission.getUserEssay())
                .isGraded(submission.getGradedAt() != null)
                .bandScore(submission.getBandScore())
                .feedback(submission.getAiFeedback())
                .submittedAt(submission.getSubmittedAt())
                .gradedAt(submission.getGradedAt())
                .build();
    }
    
    private SubmissionAdminDTO mapSpeakingToSubmissionDTO(SpeakingSubmission submission) {
        User user = submission.getAttempt().getUser();
        SpeakingPart part = submission.getPart();
        
        return SubmissionAdminDTO.builder()
                .id(submission.getId())
                .type("SPEAKING")
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userFullName(user.getFullName())
                .taskId(part.getId())
                .taskTitle("Part " + part.getPartNumber())
                .audioUrl(submission.getAudioUrl())
                .isGraded(submission.getGradedAt() != null)
                .bandScore(submission.getBandScore())
                .feedback(submission.getAiFeedback())
                .submittedAt(submission.getSubmittedAt())
                .gradedAt(submission.getGradedAt())
                .build();
    }

    public AdminStatsDTO getAdminStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfWeek = now.minusDays(7);
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                .count();
        long newUsersThisMonth = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(startOfMonth))
                .count();
        
        long totalExams = examRepository.count();
        long activeExams = examRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .count();
        
        List<UserAttempt> allAttempts = userAttemptRepository.findAll();
        long totalAttempts = allAttempts.size();
        long attemptsThisWeek = allAttempts.stream()
                .filter(a -> a.getStartedAt() != null && a.getStartedAt().isAfter(startOfWeek))
                .count();
        long attemptsThisMonth = allAttempts.stream()
                .filter(a -> a.getStartedAt() != null && a.getStartedAt().isAfter(startOfMonth))
                .count();
        
        List<MockTest> allMockTests = mockTestRepository.findAll();
        long totalMockTests = allMockTests.size();
        long gradedMockTests = allMockTests.stream()
                .filter(m -> m.getStatus() == MockTest.MockTestStatus.GRADED)
                .count();
        
        BigDecimal avgOverallBand = calculateAverageScore(allAttempts);
        
        Map<String, Long> skillDistribution = new HashMap<>();
        List<Exam> allExams = examRepository.findAll();
        for (Exam.Skill skill : Exam.Skill.values()) {
            long count = allExams.stream().filter(e -> e.getSkill() == skill).count();
            skillDistribution.put(skill.name(), count);
        }
        
        Map<String, Long> bandDistribution = new HashMap<>();
        bandDistribution.put("0-3.5", countAttemptsByBandRange(allAttempts, 0, 3.5));
        bandDistribution.put("4-5", countAttemptsByBandRange(allAttempts, 4, 5));
        bandDistribution.put("5.5-6.5", countAttemptsByBandRange(allAttempts, 5.5, 6.5));
        bandDistribution.put("7-7.5", countAttemptsByBandRange(allAttempts, 7, 7.5));
        bandDistribution.put("8-9", countAttemptsByBandRange(allAttempts, 8, 9));
        
        Map<String, BigDecimal> avgBySkill = new HashMap<>();
        for (Exam.Skill skill : Exam.Skill.values()) {
            List<Long> examIds = allExams.stream()
                    .filter(e -> e.getSkill() == skill)
                    .map(Exam::getId)
                    .collect(Collectors.toList());
            
            if (!examIds.isEmpty()) {
                List<UserAttempt> skillAttempts = allAttempts.stream()
                        .filter(a -> examIds.contains(a.getExam().getId()))
                        .collect(Collectors.toList());
                avgBySkill.put(skill.name(), calculateAverageScore(skillAttempts));
            } else {
                avgBySkill.put(skill.name(), BigDecimal.ZERO);
            }
        }
        
        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .totalExams(totalExams)
                .activeExams(activeExams)
                .totalAttempts(totalAttempts)
                .attemptsThisWeek(attemptsThisWeek)
                .attemptsThisMonth(attemptsThisMonth)
                .totalMockTests(totalMockTests)
                .gradedMockTests(gradedMockTests)
                .averageOverallBand(avgOverallBand)
                .averageSkillBands(avgBySkill)
                .skillDistribution(skillDistribution)
                .bandDistribution(bandDistribution)
                .build();
    }
    
    private BigDecimal calculateAverageScore(List<UserAttempt> attempts) {
        List<BigDecimal> scores = attempts.stream()
                .filter(a -> a.getBandScore() != null)
                .map(UserAttempt::getBandScore)
                .collect(Collectors.toList());
        
        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal sum = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
    }
    
    private long countAttemptsByBandRange(List<UserAttempt> attempts, double min, double max) {
        return attempts.stream()
                .filter(a -> a.getBandScore() != null)
                .filter(a -> {
                    double score = a.getBandScore().doubleValue();
                    return score >= min && score <= max;
                })
                .count();
    }
}
