package com.doit.service;

import com.doit.dto.placement.*;
import com.doit.entity.*;
import com.doit.exception.BadRequestException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacementTestService {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final UserAttemptRepository userAttemptRepository;
    private final ListeningQuestionRepository listeningQuestionRepository;
    private final ListeningAnswerRepository listeningAnswerRepository;
    private final ReadingQuestionRepository readingQuestionRepository;
    private final ReadingAnswerRepository readingAnswerRepository;

    private static final int PLACEMENT_QUESTIONS_PER_SKILL = 10;
    private static final int PLACEMENT_DURATION_MINUTES = 30;

    public PlacementTestDTO getPlacementTestInfo(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        List<PlacementTestDTO.PlacementSectionDTO> sections = Arrays.asList(
                PlacementTestDTO.PlacementSectionDTO.builder()
                        .skill(Exam.Skill.LISTENING)
                        .questionCount(PLACEMENT_QUESTIONS_PER_SKILL)
                        .durationMinutes(10)
                        .build(),
                PlacementTestDTO.PlacementSectionDTO.builder()
                        .skill(Exam.Skill.READING)
                        .questionCount(PLACEMENT_QUESTIONS_PER_SKILL)
                        .durationMinutes(10)
                        .build()
        );

        return PlacementTestDTO.builder()
                .id("placement-test")
                .title("IELTS Placement Test")
                .description("This test will assess your current English level.")
                .totalQuestions(PLACEMENT_QUESTIONS_PER_SKILL * 2)
                .durationMinutes(PLACEMENT_DURATION_MINUTES)
                .sections(sections)
                .build();
    }

    public String startPlacementTest(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (Boolean.TRUE.equals(user.getPlacementTestCompleted())) {
            throw new BadRequestException("Placement test already completed.");
        }

        Exam placementExam = findOrCreatePlacementExam();

        Optional<UserAttempt> existingAttempt = userAttemptRepository
                .findTopByUserIdAndExamIdAndStatusOrderByCreatedAtDesc(
                        userId, placementExam.getId(), UserAttempt.AttemptStatus.IN_PROGRESS);

        if (existingAttempt.isPresent()) {
            return existingAttempt.get().getId();
        }

        UserAttempt attempt = UserAttempt.builder()
                .userId(userId)
                .examId(placementExam.getId())
                .skill("PLACEMENT")
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(PLACEMENT_QUESTIONS_PER_SKILL * 2)
                .startedAt(LocalDateTime.now())
                .build();

        attempt = userAttemptRepository.save(attempt);
        log.info("Started placement test attempt {} for user {}", attempt.getId(), userId);

        return attempt.getId();
    }

    public PlacementResultDTO submitPlacementTest(PlacementSubmitRequest request, String userId) {
        UserAttempt attempt = userAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new BadRequestException("Attempt not found"));

        if (!attempt.getUserId().equals(userId)) {
            throw new BadRequestException("Attempt does not belong to this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        Map<String, String> answers = request.getAnswers() != null ? request.getAnswers() : new HashMap<>();

        int listeningCorrect = gradeListeningAnswers(attempt, answers);
        Double listeningScore = calculateBandScore(listeningCorrect, PLACEMENT_QUESTIONS_PER_SKILL);

        int readingCorrect = gradeReadingAnswers(attempt, answers);
        Double readingScore = calculateBandScore(readingCorrect, PLACEMENT_QUESTIONS_PER_SKILL);

        int totalCorrect = listeningCorrect + readingCorrect;
        Double overallScore = (listeningScore + readingScore) / 2;
        overallScore = Math.round(overallScore * 2) / 2.0;

        Exam.BandLevel recommendedLevel = determineLevel(overallScore);

        attempt.setStatus(UserAttempt.AttemptStatus.GRADED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setCorrectAnswers(totalCorrect);
        attempt.setBandScore(overallScore);
        userAttemptRepository.save(attempt);

        user.setCurrentBand(overallScore);
        user.setPlacementTestCompleted(true);
        userRepository.save(user);

        Map<Exam.Skill, Double> skillScores = new EnumMap<>(Exam.Skill.class);
        skillScores.put(Exam.Skill.LISTENING, listeningScore);
        skillScores.put(Exam.Skill.READING, readingScore);

        return PlacementResultDTO.builder()
                .attemptId(attempt.getId())
                .userId(userId)
                .overallBandScore(overallScore)
                .skillScores(skillScores)
                .recommendedLevel(recommendedLevel)
                .feedback(generateFeedback(overallScore))
                .strengths(identifyStrengths(listeningScore, readingScore))
                .weaknesses(identifyWeaknesses(listeningScore, readingScore))
                .studyRecommendations(generateRecommendations(recommendedLevel))
                .completedAt(LocalDateTime.now())
                .totalQuestions(PLACEMENT_QUESTIONS_PER_SKILL * 2)
                .correctAnswers(totalCorrect)
                .timeTakenSeconds(attempt.getTimeSpentSeconds())
                .build();
    }

    public PlacementResultDTO getPlacementResult(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!Boolean.TRUE.equals(user.getPlacementTestCompleted())) {
            throw new BadRequestException("Placement test not completed yet");
        }

        Exam placementExam = findOrCreatePlacementExam();
        UserAttempt attempt = userAttemptRepository
                .findTopByUserIdAndExamIdAndStatusOrderByCreatedAtDesc(
                        userId, placementExam.getId(), UserAttempt.AttemptStatus.GRADED)
                .orElseThrow(() -> new BadRequestException("No completed placement test found"));

        Double overallScore = attempt.getBandScore() != null ? attempt.getBandScore() : 0.0;
        Exam.BandLevel recommendedLevel = determineLevel(overallScore);

        Map<Exam.Skill, Double> skillScores = new EnumMap<>(Exam.Skill.class);
        skillScores.put(Exam.Skill.LISTENING, overallScore);
        skillScores.put(Exam.Skill.READING, overallScore);

        return PlacementResultDTO.builder()
                .attemptId(attempt.getId())
                .userId(userId)
                .overallBandScore(overallScore)
                .skillScores(skillScores)
                .recommendedLevel(recommendedLevel)
                .feedback(generateFeedback(overallScore))
                .strengths(identifyStrengths(overallScore, overallScore))
                .weaknesses(identifyWeaknesses(overallScore, overallScore))
                .studyRecommendations(generateRecommendations(recommendedLevel))
                .completedAt(attempt.getSubmittedAt())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .timeTakenSeconds(attempt.getTimeSpentSeconds())
                .build();
    }

    private Exam findOrCreatePlacementExam() {
        return examRepository.findByTypeAndIsActiveTrue(Exam.ExamType.PRACTICE, null)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Exam exam = Exam.builder()
                            .title("Placement Test")
                            .description("IELTS Placement Assessment")
                            .type(Exam.ExamType.PRACTICE)
                            .skill(Exam.Skill.LISTENING)
                            .bandLevel(Exam.BandLevel.ELEMENTARY)
                            .durationMinutes(PLACEMENT_DURATION_MINUTES)
                            .isActive(true)
                            .build();
                    return examRepository.save(exam);
                });
    }

    private int gradeListeningAnswers(UserAttempt attempt, Map<String, String> answers) {
        int correct = 0;
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            Optional<ListeningQuestion> question = listeningQuestionRepository.findById(entry.getKey());
            if (question.isPresent()) {
                String correctAnswer = question.get().getCorrectAnswer();
                boolean isCorrect = correctAnswer != null && 
                        correctAnswer.trim().equalsIgnoreCase(entry.getValue().trim());
                if (isCorrect) correct++;
                
                ListeningAnswer answer = ListeningAnswer.builder()
                        .attemptId(attempt.getId())
                        .questionId(entry.getKey())
                        .userAnswer(entry.getValue())
                        .isCorrect(isCorrect)
                        .build();
                listeningAnswerRepository.save(answer);
            }
        }
        return Math.min(correct, PLACEMENT_QUESTIONS_PER_SKILL);
    }

    private int gradeReadingAnswers(UserAttempt attempt, Map<String, String> answers) {
        int correct = 0;
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            Optional<ReadingQuestion> question = readingQuestionRepository.findById(entry.getKey());
            if (question.isPresent()) {
                String correctAnswer = question.get().getCorrectAnswer();
                boolean isCorrect = correctAnswer != null && 
                        correctAnswer.trim().equalsIgnoreCase(entry.getValue().trim());
                if (isCorrect) correct++;
                
                ReadingAnswer answer = ReadingAnswer.builder()
                        .attemptId(attempt.getId())
                        .questionId(entry.getKey())
                        .userAnswer(entry.getValue())
                        .isCorrect(isCorrect)
                        .build();
                readingAnswerRepository.save(answer);
            }
        }
        return Math.min(correct, PLACEMENT_QUESTIONS_PER_SKILL);
    }

    private Double calculateBandScore(int correct, int total) {
        if (total == 0) return 4.0;
        double percentage = (double) correct / total * 100;
        if (percentage >= 90) return 9.0;
        if (percentage >= 80) return 8.0;
        if (percentage >= 70) return 7.0;
        if (percentage >= 60) return 6.0;
        if (percentage >= 50) return 5.0;
        if (percentage >= 40) return 4.5;
        if (percentage >= 30) return 4.0;
        return 3.5;
    }

    private Exam.BandLevel determineLevel(Double score) {
        if (score >= 7.5) return Exam.BandLevel.ADVANCED;
        if (score >= 6.5) return Exam.BandLevel.INTERMEDIATE;
        if (score >= 5.0) return Exam.BandLevel.ELEMENTARY;
        return Exam.BandLevel.BEGINNER;
    }

    private String generateFeedback(Double overall) {
        if (overall >= 7.0) return "Excellent! You're ready for advanced IELTS preparation.";
        if (overall >= 5.0) return "Good foundation! Focus on building vocabulary.";
        return "Start with foundational exercises to build your skills.";
    }

    private String identifyStrengths(Double listening, Double reading) {
        List<String> strengths = new ArrayList<>();
        if (listening >= 6.0) strengths.add("Good listening comprehension");
        if (reading >= 6.0) strengths.add("Solid reading skills");
        return strengths.isEmpty() ? "Keep practicing" : String.join(", ", strengths);
    }

    private String identifyWeaknesses(Double listening, Double reading) {
        List<String> weaknesses = new ArrayList<>();
        if (listening < 5.0) weaknesses.add("Listening needs improvement");
        if (reading < 5.0) weaknesses.add("Reading needs development");
        return weaknesses.isEmpty() ? "No major weaknesses" : String.join(", ", weaknesses);
    }

    private String generateRecommendations(Exam.BandLevel level) {
        switch (level) {
            case ADVANCED: return "Focus on advanced vocabulary and complex structures";
            case INTERMEDIATE: return "Build academic vocabulary systematically";
            case ELEMENTARY: return "Build core vocabulary and grammar";
            default: return "Start with basic grammar and vocabulary";
        }
    }
}
