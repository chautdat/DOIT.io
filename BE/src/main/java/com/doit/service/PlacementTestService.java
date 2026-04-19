package com.doit.service;

import com.doit.dto.placement.*;
import com.doit.entity.*;
import com.doit.exception.BadRequestException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
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

    /**
     * Get placement test info for user
     */
    @Transactional(readOnly = true)
    public PlacementTestDTO getPlacementTestInfo(Long userId) {
        User user = userRepository.findById(userId)
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
                .id(0L)
                .title("IELTS Placement Test")
                .description("This test will assess your current English level across Listening and Reading skills to recommend appropriate study materials.")
                .totalQuestions(PLACEMENT_QUESTIONS_PER_SKILL * 2)
                .durationMinutes(PLACEMENT_DURATION_MINUTES)
                .sections(sections)
                .build();
    }

    /**
     * Start a placement test attempt
     */
    public Long startPlacementTest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (Boolean.TRUE.equals(user.getPlacementTestCompleted())) {
            throw new BadRequestException("Placement test already completed. Contact support to retake.");
        }

        // Find or create a placement exam
        Exam placementExam = findOrCreatePlacementExam();

        // Check for existing in-progress attempt
        Optional<UserAttempt> existingAttempt = userAttemptRepository
                .findTopByUserAndExamAndStatusOrderByCreatedAtDesc(user, placementExam, UserAttempt.AttemptStatus.IN_PROGRESS);

        if (existingAttempt.isPresent()) {
            return existingAttempt.get().getId();
        }

        // Create new attempt
        UserAttempt attempt = UserAttempt.builder()
                .user(user)
                .exam(placementExam)
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(PLACEMENT_QUESTIONS_PER_SKILL * 2)
                .build();

        attempt = userAttemptRepository.save(attempt);
        log.info("Started placement test attempt {} for user {}", attempt.getId(), userId);

        return attempt.getId();
    }

    /**
     * Submit placement test and get results
     */
    public PlacementResultDTO submitPlacementTest(PlacementSubmitRequest request, Long userId) {
        UserAttempt attempt = userAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new BadRequestException("Attempt not found"));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new BadRequestException("Attempt does not belong to this user");
        }

        if (attempt.getStatus() == UserAttempt.AttemptStatus.SUBMITTED ||
            attempt.getStatus() == UserAttempt.AttemptStatus.GRADED) {
            throw new BadRequestException("Placement test already submitted");
        }

        User user = attempt.getUser();
        Map<Long, String> answers = request.getAnswers() != null ? request.getAnswers() : new HashMap<>();

        // Grade listening questions
        int listeningCorrect = gradeListeningAnswers(attempt, answers);
        BigDecimal listeningScore = calculateBandScore(listeningCorrect, PLACEMENT_QUESTIONS_PER_SKILL);

        // Grade reading questions
        int readingCorrect = gradeReadingAnswers(attempt, answers);
        BigDecimal readingScore = calculateBandScore(readingCorrect, PLACEMENT_QUESTIONS_PER_SKILL);

        // Calculate overall score
        int totalCorrect = listeningCorrect + readingCorrect;
        BigDecimal overallScore = listeningScore.add(readingScore)
                .divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP);

        // Round to nearest 0.5
        double rawScore = overallScore.doubleValue();
        double rounded = Math.round(rawScore * 2) / 2.0;
        overallScore = BigDecimal.valueOf(rounded).setScale(1, RoundingMode.HALF_UP);

        // Determine recommended level
        Exam.BandLevel recommendedLevel = determineLevel(overallScore);

        // Update attempt
        attempt.setStatus(UserAttempt.AttemptStatus.GRADED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setCorrectAnswers(totalCorrect);
        attempt.setBandScore(overallScore);
        userAttemptRepository.save(attempt);

        // Update user profile
        user.setCurrentBand(overallScore.doubleValue());
        user.setPlacementTestCompleted(true);
        userRepository.save(user);

        log.info("Completed placement test for user {} with score {}", userId, overallScore);

        // Build skill scores map
        Map<Exam.Skill, BigDecimal> skillScores = new EnumMap<>(Exam.Skill.class);
        skillScores.put(Exam.Skill.LISTENING, listeningScore);
        skillScores.put(Exam.Skill.READING, readingScore);

        // Generate feedback
        String feedback = generateFeedback(overallScore, listeningScore, readingScore);
        String strengths = identifyStrengths(listeningScore, readingScore);
        String weaknesses = identifyWeaknesses(listeningScore, readingScore);
        String recommendations = generateRecommendations(recommendedLevel, listeningScore, readingScore);

        return PlacementResultDTO.builder()
                .attemptId(attempt.getId())
                .userId(userId)
                .overallBandScore(overallScore)
                .skillScores(skillScores)
                .recommendedLevel(recommendedLevel)
                .feedback(feedback)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .studyRecommendations(recommendations)
                .completedAt(LocalDateTime.now())
                .totalQuestions(PLACEMENT_QUESTIONS_PER_SKILL * 2)
                .correctAnswers(totalCorrect)
                .timeTakenSeconds(attempt.getTimeSpentSeconds())
                .build();
    }

    /**
     * Get placement test result
     */
    @Transactional(readOnly = true)
    public PlacementResultDTO getPlacementResult(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!Boolean.TRUE.equals(user.getPlacementTestCompleted())) {
            throw new BadRequestException("Placement test not completed yet");
        }

        // Find the latest graded placement attempt
        Exam placementExam = findOrCreatePlacementExam();
        UserAttempt attempt = userAttemptRepository
                .findTopByUserAndExamAndStatusOrderByCreatedAtDesc(user, placementExam, UserAttempt.AttemptStatus.GRADED)
                .orElseThrow(() -> new BadRequestException("No completed placement test found"));

        BigDecimal overallScore = attempt.getBandScore() != null ? attempt.getBandScore() : BigDecimal.ZERO;
        Exam.BandLevel recommendedLevel = determineLevel(overallScore);

        // Estimate skill scores (simplified - in real app would store separately)
        BigDecimal estimatedSkillScore = overallScore;
        Map<Exam.Skill, BigDecimal> skillScores = new EnumMap<>(Exam.Skill.class);
        skillScores.put(Exam.Skill.LISTENING, estimatedSkillScore);
        skillScores.put(Exam.Skill.READING, estimatedSkillScore);

        return PlacementResultDTO.builder()
                .attemptId(attempt.getId())
                .userId(userId)
                .overallBandScore(overallScore)
                .skillScores(skillScores)
                .recommendedLevel(recommendedLevel)
                .feedback(generateFeedback(overallScore, estimatedSkillScore, estimatedSkillScore))
                .strengths(identifyStrengths(estimatedSkillScore, estimatedSkillScore))
                .weaknesses(identifyWeaknesses(estimatedSkillScore, estimatedSkillScore))
                .studyRecommendations(generateRecommendations(recommendedLevel, estimatedSkillScore, estimatedSkillScore))
                .completedAt(attempt.getSubmittedAt())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .timeTakenSeconds(attempt.getTimeSpentSeconds())
                .build();
    }

    // ==================== Private Helper Methods ====================

    private Exam findOrCreatePlacementExam() {
        // In real implementation, would have a dedicated placement exam
        // For now, return first PRACTICE exam or create placeholder
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

    private int gradeListeningAnswers(UserAttempt attempt, Map<Long, String> answers) {
        // Simplified grading - in real app would fetch actual questions
        int correct = 0;
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            // Check if this is a listening question and grade it
            Optional<ListeningQuestion> question = listeningQuestionRepository.findById(entry.getKey());
            if (question.isPresent()) {
                String correctAnswer = question.get().getCorrectAnswer();
                if (correctAnswer != null && correctAnswer.trim().equalsIgnoreCase(entry.getValue().trim())) {
                    correct++;
                }
                
                // Save answer
                ListeningAnswer answer = ListeningAnswer.builder()
                        .attempt(attempt)
                        .question(question.get())
                        .userAnswer(entry.getValue())
                        .isCorrect(correctAnswer != null && correctAnswer.trim().equalsIgnoreCase(entry.getValue().trim()))
                        .build();
                listeningAnswerRepository.save(answer);
            }
        }
        return Math.min(correct, PLACEMENT_QUESTIONS_PER_SKILL);
    }

    private int gradeReadingAnswers(UserAttempt attempt, Map<Long, String> answers) {
        int correct = 0;
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Optional<ReadingQuestion> question = readingQuestionRepository.findById(entry.getKey());
            if (question.isPresent()) {
                String correctAnswer = question.get().getCorrectAnswer();
                if (correctAnswer != null && correctAnswer.trim().equalsIgnoreCase(entry.getValue().trim())) {
                    correct++;
                }
                
                ReadingAnswer answer = ReadingAnswer.builder()
                        .attempt(attempt)
                        .question(question.get())
                        .userAnswer(entry.getValue())
                        .isCorrect(correctAnswer != null && correctAnswer.trim().equalsIgnoreCase(entry.getValue().trim()))
                        .build();
                readingAnswerRepository.save(answer);
            }
        }
        return Math.min(correct, PLACEMENT_QUESTIONS_PER_SKILL);
    }

    private BigDecimal calculateBandScore(int correct, int total) {
        if (total == 0) return BigDecimal.valueOf(4.0);
        
        double percentage = (double) correct / total * 100;
        
        // Simplified IELTS-style band calculation
        if (percentage >= 90) return BigDecimal.valueOf(9.0);
        if (percentage >= 80) return BigDecimal.valueOf(8.0);
        if (percentage >= 70) return BigDecimal.valueOf(7.0);
        if (percentage >= 60) return BigDecimal.valueOf(6.0);
        if (percentage >= 50) return BigDecimal.valueOf(5.0);
        if (percentage >= 40) return BigDecimal.valueOf(4.5);
        if (percentage >= 30) return BigDecimal.valueOf(4.0);
        return BigDecimal.valueOf(3.5);
    }

    private Exam.BandLevel determineLevel(BigDecimal score) {
        double s = score.doubleValue();
        if (s >= 7.5) return Exam.BandLevel.ADVANCED;
        if (s >= 6.5) return Exam.BandLevel.INTERMEDIATE;
        if (s >= 5.0) return Exam.BandLevel.ELEMENTARY;
        return Exam.BandLevel.BEGINNER;
    }

    private String generateFeedback(BigDecimal overall, BigDecimal listening, BigDecimal reading) {
        StringBuilder fb = new StringBuilder();
        fb.append("## Placement Test Results\n\n");
        fb.append("**Overall Band Score: ").append(overall).append("**\n\n");
        
        if (overall.doubleValue() >= 7.0) {
            fb.append("Excellent! You demonstrate strong English proficiency. ");
            fb.append("You're ready for advanced IELTS preparation.\n");
        } else if (overall.doubleValue() >= 5.0) {
            fb.append("Good foundation! You have intermediate English skills. ");
            fb.append("Focus on building vocabulary and improving accuracy.\n");
        } else {
            fb.append("You're at a developing level. ");
            fb.append("Start with foundational exercises to build your skills.\n");
        }
        
        return fb.toString();
    }

    private String identifyStrengths(BigDecimal listening, BigDecimal reading) {
        List<String> strengths = new ArrayList<>();
        
        if (listening.doubleValue() >= 6.0) {
            strengths.add("Good listening comprehension");
        }
        if (reading.doubleValue() >= 6.0) {
            strengths.add("Solid reading skills");
        }
        if (listening.compareTo(reading) > 0) {
            strengths.add("Listening is your stronger skill");
        } else if (reading.compareTo(listening) > 0) {
            strengths.add("Reading is your stronger skill");
        }
        
        return strengths.isEmpty() ? "Keep practicing to develop your strengths" : String.join(", ", strengths);
    }

    private String identifyWeaknesses(BigDecimal listening, BigDecimal reading) {
        List<String> weaknesses = new ArrayList<>();
        
        if (listening.doubleValue() < 5.0) {
            weaknesses.add("Listening comprehension needs improvement");
        }
        if (reading.doubleValue() < 5.0) {
            weaknesses.add("Reading skills need development");
        }
        
        return weaknesses.isEmpty() ? "No major weaknesses identified" : String.join(", ", weaknesses);
    }

    private String generateRecommendations(Exam.BandLevel level, BigDecimal listening, BigDecimal reading) {
        StringBuilder rec = new StringBuilder();
        rec.append("### Recommended Study Plan\n\n");
        
        switch (level) {
            case ADVANCED:
                rec.append("- Focus on advanced vocabulary and complex sentence structures\n");
                rec.append("- Practice with authentic IELTS materials\n");
                rec.append("- Take full mock tests regularly\n");
                break;
            case INTERMEDIATE:
                rec.append("- Build academic vocabulary systematically\n");
                rec.append("- Practice skimming and scanning techniques\n");
                rec.append("- Work on note-taking skills for listening\n");
                break;
            case ELEMENTARY:
                rec.append("- Build core vocabulary and grammar\n");
                rec.append("- Practice with graded materials\n");
                rec.append("- Focus on common IELTS question types\n");
                break;
            default:
                rec.append("- Start with basic grammar and vocabulary\n");
                rec.append("- Practice with simplified materials first\n");
                rec.append("- Focus on building foundational skills\n");
        }
        
        if (listening.compareTo(reading) < 0) {
            rec.append("- **Priority:** More listening practice recommended\n");
        } else if (reading.compareTo(listening) < 0) {
            rec.append("- **Priority:** More reading practice recommended\n");
        }
        
        return rec.toString();
    }
}
