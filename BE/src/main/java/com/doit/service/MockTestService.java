package com.doit.service;

import com.doit.dto.mocktest.*;
import com.doit.entity.*;
import com.doit.exception.ResourceNotFoundException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockTestService {

    private final MockTestRepository mockTestRepository;
    private final UserAttemptRepository userAttemptRepository;

    public MockTestDTO startMockTest(User user) {
        MockTest mockTest = MockTest.builder()
                .userId(user.getId())
                .status(MockTest.MockTestStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();
        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Started new mock test {} for user {}", saved.getId(), user.getId());
        return toDTO(saved);
    }

    public MockTestDTO startMockTestWithExams(User user, MockTestCreateRequest request) {
        MockTest mockTest = MockTest.builder()
                .userId(user.getId())
                .listeningExamId(request.getListeningExamId())
                .readingExamId(request.getReadingExamId())
                .writingExamId(request.getWritingExamId())
                .speakingExamId(request.getSpeakingExamId())
                .status(MockTest.MockTestStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();
        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Started mock test {} with pre-selected exams for user {}", saved.getId(), user.getId());
        return toDTO(saved);
    }

    public MockTestDTO getMockTest(String mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUserId(mockTestId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));
        return toDTO(mockTest);
    }

    public List<MockTestDTO> getUserMockTests(User user) {
        return mockTestRepository.findByUserIdOrderByStartedAtDesc(user.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<MockTestDTO> getUserMockTests(User user, Pageable pageable) {
        return mockTestRepository.findByUserId(user.getId(), pageable).map(this::toDTO);
    }

    public MockTestDTO submitSkillAttempt(String mockTestId, User user, MockTestSkillSubmitRequest request) {
        MockTest mockTest = mockTestRepository.findByIdAndUserId(mockTestId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));

        UserAttempt attempt = userAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("User attempt not found"));

        if (!attempt.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("Attempt does not belong to user");
        }

        Double bandScore = request.getBandScore() != null ? request.getBandScore() : attempt.getBandScore();

        switch (request.getSkill()) {
            case LISTENING:
                mockTest.setListeningScore(bandScore);
                break;
            case READING:
                mockTest.setReadingScore(bandScore);
                break;
            case WRITING:
                mockTest.setWritingScore(bandScore);
                break;
            case SPEAKING:
                mockTest.setSpeakingScore(bandScore);
                break;
            default:
                throw new IllegalArgumentException("Invalid skill: " + request.getSkill());
        }

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Submitted {} attempt for mock test {}", request.getSkill(), mockTestId);
        return toDTO(saved);
    }

    public MockTestResultDTO completeMockTest(String mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUserId(mockTestId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));

        double total = 0;
        int count = 0;
        if (mockTest.getListeningScore() != null) { total += mockTest.getListeningScore(); count++; }
        if (mockTest.getReadingScore() != null) { total += mockTest.getReadingScore(); count++; }
        if (mockTest.getWritingScore() != null) { total += mockTest.getWritingScore(); count++; }
        if (mockTest.getSpeakingScore() != null) { total += mockTest.getSpeakingScore(); count++; }

        double overallBand = count > 0 ? Math.round(total / count * 2) / 2.0 : 0;
        mockTest.setOverallBand(overallBand);
        mockTest.setTotalScore(total);
        mockTest.setStatus(MockTest.MockTestStatus.COMPLETED);
        mockTest.setCompletedAt(LocalDateTime.now());

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Completed mock test {} with overall band {}", mockTestId, overallBand);

        return toResultDTO(saved);
    }

    public List<MockTestDTO> getInProgressMockTests(User user) {
        return mockTestRepository.findByUserIdAndStatus(user.getId(), MockTest.MockTestStatus.IN_PROGRESS)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MockTestResultDTO getMockTestResult(String mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUserId(mockTestId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));
        return toResultDTO(mockTest);
    }

    public void deleteMockTest(String mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUserId(mockTestId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));
        mockTestRepository.delete(mockTest);
        log.info("Deleted mock test {}", mockTestId);
    }

    public MockTestResultDTO gradeMockTest(String mockTestId, Double listeningBand, Double readingBand, 
                                           Double writingBand, Double speakingBand) {
        MockTest mockTest = mockTestRepository.findById(mockTestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));
        
        if (listeningBand != null) mockTest.setListeningScore(listeningBand);
        if (readingBand != null) mockTest.setReadingScore(readingBand);
        if (writingBand != null) mockTest.setWritingScore(writingBand);
        if (speakingBand != null) mockTest.setSpeakingScore(speakingBand);

        double total = 0;
        int count = 0;
        if (mockTest.getListeningScore() != null) { total += mockTest.getListeningScore(); count++; }
        if (mockTest.getReadingScore() != null) { total += mockTest.getReadingScore(); count++; }
        if (mockTest.getWritingScore() != null) { total += mockTest.getWritingScore(); count++; }
        if (mockTest.getSpeakingScore() != null) { total += mockTest.getSpeakingScore(); count++; }

        if (count == 4) {
            double overallBand = Math.round(total / count * 2) / 2.0;
            mockTest.setOverallBand(overallBand);
            mockTest.setTotalScore(total);
            mockTest.setStatus(MockTest.MockTestStatus.COMPLETED);
            mockTest.setCompletedAt(LocalDateTime.now());
        }

        MockTest saved = mockTestRepository.save(mockTest);
        log.info("Graded mock test {}", mockTestId);
        return toResultDTO(saved);
    }

    public void cancelMockTest(String mockTestId, User user) {
        MockTest mockTest = mockTestRepository.findByIdAndUserId(mockTestId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found"));
        mockTest.setStatus(MockTest.MockTestStatus.CANCELLED);
        mockTestRepository.save(mockTest);
        log.info("Cancelled mock test {}", mockTestId);
    }

    private MockTestDTO toDTO(MockTest mockTest) {
        return MockTestDTO.builder()
                .id(mockTest.getId())
                .userId(mockTest.getUserId())
                .listeningExamId(mockTest.getListeningExamId())
                .readingExamId(mockTest.getReadingExamId())
                .writingExamId(mockTest.getWritingExamId())
                .speakingExamId(mockTest.getSpeakingExamId())
                .status(mockTest.getStatus().name())
                .listeningScore(mockTest.getListeningScore())
                .readingScore(mockTest.getReadingScore())
                .writingScore(mockTest.getWritingScore())
                .speakingScore(mockTest.getSpeakingScore())
                .overallBand(mockTest.getOverallBand())
                .startedAt(mockTest.getStartedAt())
                .completedAt(mockTest.getCompletedAt())
                .build();
    }

    private MockTestResultDTO toResultDTO(MockTest mockTest) {
        return MockTestResultDTO.builder()
                .id(mockTest.getId())
                .listeningScore(mockTest.getListeningScore())
                .readingScore(mockTest.getReadingScore())
                .writingScore(mockTest.getWritingScore())
                .speakingScore(mockTest.getSpeakingScore())
                .overallBand(mockTest.getOverallBand())
                .completedAt(mockTest.getCompletedAt())
                .build();
    }
}
