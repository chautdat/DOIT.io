package com.doit.service;

import com.doit.dto.mocktest.MockTestCreateRequest;
import com.doit.dto.mocktest.MockTestDTO;
import com.doit.dto.mocktest.MockTestResultDTO;
import com.doit.dto.mocktest.MockTestSkillSubmitRequest;
import com.doit.entity.Exam;
import com.doit.entity.MockTest;
import com.doit.entity.User;
import com.doit.entity.UserAttempt;
import com.doit.exception.ResourceNotFoundException;
import com.doit.repository.MockTestRepository;
import com.doit.repository.UserAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MockTestService {

    private final MockTestRepository mockTestRepository;
    private final UserAttemptRepository userAttemptRepository;

    /**
     * Start a new mock test for the user
     */
    public MockTestDTO startMockTest(User user) {
        MockTest mockTest = MockTest.builder()
                .user(user)
                .status(MockTest.MockTestStatus.IN_PROGRESS)
                .build();

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Started new mock test {} for user {}", saved.getId(), user.getId());

        return toDTO(saved);
    }

    /**
     * Start a mock test with pre-selected exams
     */
    public MockTestDTO startMockTestWithExams(User user, MockTestCreateRequest request) {
        MockTest mockTest = MockTest.builder()
                .user(user)
                .status(MockTest.MockTestStatus.IN_PROGRESS)
                .build();

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Started mock test {} with pre-selected exams for user {}", saved.getId(), user.getId());

        return toDTO(saved);
    }

    /**
     * Get mock test by ID for user
     */
    @Transactional(readOnly = true)
    public MockTestDTO getMockTest(Long mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUser(mockTestId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));
        return toDTO(mockTest);
    }

    /**
     * Get all mock tests for user
     */
    @Transactional(readOnly = true)
    public List<MockTestDTO> getUserMockTests(User user) {
        return mockTestRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated mock tests for user
     */
    @Transactional(readOnly = true)
    public Page<MockTestDTO> getUserMockTests(User user, Pageable pageable) {
        return mockTestRepository.findByUser(user, pageable)
                .map(this::toDTO);
    }

    /**
     * Submit a skill attempt for the mock test
     */
    public MockTestDTO submitSkillAttempt(Long mockTestId, User user, MockTestSkillSubmitRequest request) {
        MockTest mockTest = mockTestRepository.findByIdAndUser(mockTestId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));

        UserAttempt attempt = userAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("User attempt not found"));

        // Verify attempt belongs to user
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Attempt does not belong to user");
        }

        BigDecimal bandScore = request.getBandScore() != null ? request.getBandScore() : attempt.getBandScore();

        switch (request.getSkill()) {
            case LISTENING:
                mockTest.setListeningAttempt(attempt);
                mockTest.setListeningBand(bandScore);
                mockTest.setStatus(MockTest.MockTestStatus.LISTENING);
                break;
            case READING:
                mockTest.setReadingAttempt(attempt);
                mockTest.setReadingBand(bandScore);
                mockTest.setStatus(MockTest.MockTestStatus.READING);
                break;
            case WRITING:
                mockTest.setWritingAttempt(attempt);
                mockTest.setWritingBand(bandScore);
                mockTest.setStatus(MockTest.MockTestStatus.WRITING);
                break;
            case SPEAKING:
                mockTest.setSpeakingAttempt(attempt);
                mockTest.setSpeakingBand(bandScore);
                mockTest.setStatus(MockTest.MockTestStatus.SPEAKING);
                break;
            default:
                throw new IllegalArgumentException("Invalid skill: " + request.getSkill());
        }

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Submitted {} attempt for mock test {}", request.getSkill(), mockTestId);

        return toDTO(saved);
    }

    /**
     * Complete the mock test and calculate overall band
     */
    public MockTestResultDTO completeMockTest(Long mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUser(mockTestId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));

        // Calculate overall band score (average of 4 skills)
        BigDecimal overallBand = calculateOverallBand(mockTest);
        mockTest.setOverallBand(overallBand);
        mockTest.setStatus(MockTest.MockTestStatus.COMPLETED);
        mockTest.setCompletedAt(LocalDateTime.now());

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Completed mock test {} with overall band {}", mockTestId, overallBand);

        return toResultDTO(saved);
    }

    /**
     * Grade the mock test (by admin/teacher)
     */
    public MockTestResultDTO gradeMockTest(Long mockTestId, BigDecimal listeningBand, BigDecimal readingBand,
                                           BigDecimal writingBand, BigDecimal speakingBand) {
        MockTest mockTest = mockTestRepository.findById(mockTestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));

        if (listeningBand != null) mockTest.setListeningBand(listeningBand);
        if (readingBand != null) mockTest.setReadingBand(readingBand);
        if (writingBand != null) mockTest.setWritingBand(writingBand);
        if (speakingBand != null) mockTest.setSpeakingBand(speakingBand);

        BigDecimal overallBand = calculateOverallBand(mockTest);
        mockTest.setOverallBand(overallBand);
        mockTest.setStatus(MockTest.MockTestStatus.GRADED);
        if (mockTest.getCompletedAt() == null) {
            mockTest.setCompletedAt(LocalDateTime.now());
        }

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Graded mock test {} with overall band {}", mockTestId, overallBand);

        return toResultDTO(saved);
    }

    /**
     * Get mock test result
     */
    @Transactional(readOnly = true)
    public MockTestResultDTO getMockTestResult(Long mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUser(mockTestId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));

        if (mockTest.getStatus() != MockTest.MockTestStatus.COMPLETED &&
            mockTest.getStatus() != MockTest.MockTestStatus.GRADED) {
            throw new IllegalStateException("Mock test is not yet completed");
        }

        return toResultDTO(mockTest);
    }

    /**
     * Get in-progress mock tests for user
     */
    @Transactional(readOnly = true)
    public List<MockTestDTO> getInProgressMockTests(User user) {
        return mockTestRepository.findByUserAndStatus(user, MockTest.MockTestStatus.IN_PROGRESS)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete mock test
     */
    public void deleteMockTest(Long mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUser(mockTestId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));

        mockTestRepository.delete(mockTest);
        log.info("Deleted mock test {} for user {}", mockTestId, user.getId());
    }

    /**
     * Calculate overall band score from 4 skills
     */
    private BigDecimal calculateOverallBand(MockTest mockTest) {
        BigDecimal listening = mockTest.getListeningBand() != null ? mockTest.getListeningBand() : BigDecimal.ZERO;
        BigDecimal reading = mockTest.getReadingBand() != null ? mockTest.getReadingBand() : BigDecimal.ZERO;
        BigDecimal writing = mockTest.getWritingBand() != null ? mockTest.getWritingBand() : BigDecimal.ZERO;
        BigDecimal speaking = mockTest.getSpeakingBand() != null ? mockTest.getSpeakingBand() : BigDecimal.ZERO;

        BigDecimal sum = listening.add(reading).add(writing).add(speaking);
        BigDecimal average = sum.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);

        // Round to nearest 0.5 (IELTS standard)
        return roundToHalf(average);
    }

    /**
     * Round to nearest 0.5 (IELTS standard)
     */
    private BigDecimal roundToHalf(BigDecimal value) {
        BigDecimal doubled = value.multiply(BigDecimal.valueOf(2));
        BigDecimal rounded = doubled.setScale(0, RoundingMode.HALF_UP);
        return rounded.divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP);
    }

    /**
     * Generate feedback based on band scores
     */
    private String generateOverallFeedback(MockTest mockTest) {
        BigDecimal overallBand = mockTest.getOverallBand();
        if (overallBand == null) return "No feedback available";

        double band = overallBand.doubleValue();

        if (band >= 8.0) {
            return "Excellent performance! You demonstrate expert user level proficiency. "
                    + "Continue maintaining your skills with advanced practice materials.";
        } else if (band >= 7.0) {
            return "Very good performance! You are a good user of English. "
                    + "Focus on refining accuracy and expanding vocabulary for even better results.";
        } else if (band >= 6.0) {
            return "Good performance! You are a competent user. "
                    + "Work on improving consistency across all skills and expanding academic vocabulary.";
        } else if (band >= 5.0) {
            return "Moderate performance. You have partial command of the language. "
                    + "Focus on building fundamental skills and practicing regularly.";
        } else {
            return "Keep practicing! Focus on building basic language skills across all areas. "
                    + "Consider taking a structured IELTS preparation course.";
        }
    }

    /**
     * Identify strengths based on band scores
     */
    private String identifyStrengths(MockTest mockTest) {
        Map<String, BigDecimal> scores = new HashMap<>();
        if (mockTest.getListeningBand() != null) scores.put("Listening", mockTest.getListeningBand());
        if (mockTest.getReadingBand() != null) scores.put("Reading", mockTest.getReadingBand());
        if (mockTest.getWritingBand() != null) scores.put("Writing", mockTest.getWritingBand());
        if (mockTest.getSpeakingBand() != null) scores.put("Speaking", mockTest.getSpeakingBand());

        if (scores.isEmpty()) return "Complete all skills to see strengths";

        BigDecimal maxScore = scores.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        List<String> strengths = scores.entrySet().stream()
                .filter(e -> e.getValue().compareTo(maxScore) == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return "Your strongest skill(s): " + String.join(", ", strengths)
                + " (Band " + maxScore + ")";
    }

    /**
     * Identify areas for improvement
     */
    private String identifyAreasForImprovement(MockTest mockTest) {
        Map<String, BigDecimal> scores = new HashMap<>();
        if (mockTest.getListeningBand() != null) scores.put("Listening", mockTest.getListeningBand());
        if (mockTest.getReadingBand() != null) scores.put("Reading", mockTest.getReadingBand());
        if (mockTest.getWritingBand() != null) scores.put("Writing", mockTest.getWritingBand());
        if (mockTest.getSpeakingBand() != null) scores.put("Speaking", mockTest.getSpeakingBand());

        if (scores.isEmpty()) return "Complete all skills to see areas for improvement";

        BigDecimal minScore = scores.values().stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        List<String> improvements = scores.entrySet().stream()
                .filter(e -> e.getValue().compareTo(minScore) == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return "Focus on improving: " + String.join(", ", improvements)
                + " (Currently Band " + minScore + ")";
    }

    /**
     * Convert MockTest to DTO
     */
    private MockTestDTO toDTO(MockTest mockTest) {
        Integer totalTimeMinutes = null;
        if (mockTest.getStartedAt() != null && mockTest.getCompletedAt() != null) {
            totalTimeMinutes = (int) Duration.between(
                    mockTest.getStartedAt(), mockTest.getCompletedAt()).toMinutes();
        }

        return MockTestDTO.builder()
                .id(mockTest.getId())
                .userId(mockTest.getUser().getId())
                .status(mockTest.getStatus())
                .listeningBand(mockTest.getListeningBand())
                .readingBand(mockTest.getReadingBand())
                .writingBand(mockTest.getWritingBand())
                .speakingBand(mockTest.getSpeakingBand())
                .overallBand(mockTest.getOverallBand())
                .listeningAttemptId(mockTest.getListeningAttempt() != null ?
                        mockTest.getListeningAttempt().getId() : null)
                .readingAttemptId(mockTest.getReadingAttempt() != null ?
                        mockTest.getReadingAttempt().getId() : null)
                .writingAttemptId(mockTest.getWritingAttempt() != null ?
                        mockTest.getWritingAttempt().getId() : null)
                .speakingAttemptId(mockTest.getSpeakingAttempt() != null ?
                        mockTest.getSpeakingAttempt().getId() : null)
                .startedAt(mockTest.getStartedAt())
                .completedAt(mockTest.getCompletedAt())
                .totalTimeMinutes(totalTimeMinutes)
                .build();
    }

    /**
     * Convert MockTest to Result DTO
     */
    private MockTestResultDTO toResultDTO(MockTest mockTest) {
        Map<Exam.Skill, BigDecimal> skillBands = new HashMap<>();
        if (mockTest.getListeningBand() != null)
            skillBands.put(Exam.Skill.LISTENING, mockTest.getListeningBand());
        if (mockTest.getReadingBand() != null)
            skillBands.put(Exam.Skill.READING, mockTest.getReadingBand());
        if (mockTest.getWritingBand() != null)
            skillBands.put(Exam.Skill.WRITING, mockTest.getWritingBand());
        if (mockTest.getSpeakingBand() != null)
            skillBands.put(Exam.Skill.SPEAKING, mockTest.getSpeakingBand());

        Map<Exam.Skill, Long> attemptIds = new HashMap<>();
        if (mockTest.getListeningAttempt() != null)
            attemptIds.put(Exam.Skill.LISTENING, mockTest.getListeningAttempt().getId());
        if (mockTest.getReadingAttempt() != null)
            attemptIds.put(Exam.Skill.READING, mockTest.getReadingAttempt().getId());
        if (mockTest.getWritingAttempt() != null)
            attemptIds.put(Exam.Skill.WRITING, mockTest.getWritingAttempt().getId());
        if (mockTest.getSpeakingAttempt() != null)
            attemptIds.put(Exam.Skill.SPEAKING, mockTest.getSpeakingAttempt().getId());

        Integer totalTimeMinutes = null;
        if (mockTest.getStartedAt() != null && mockTest.getCompletedAt() != null) {
            totalTimeMinutes = (int) Duration.between(
                    mockTest.getStartedAt(), mockTest.getCompletedAt()).toMinutes();
        }

        return MockTestResultDTO.builder()
                .mockTestId(mockTest.getId())
                .userId(mockTest.getUser().getId())
                .overallBand(mockTest.getOverallBand())
                .skillBands(skillBands)
                .attemptIds(attemptIds)
                .overallFeedback(generateOverallFeedback(mockTest))
                .strengths(identifyStrengths(mockTest))
                .areasForImprovement(identifyAreasForImprovement(mockTest))
                .startedAt(mockTest.getStartedAt())
                .completedAt(mockTest.getCompletedAt())
                .totalTimeMinutes(totalTimeMinutes)
                .build();
    }
}
