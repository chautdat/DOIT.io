package com.doit.service;

import com.doit.dto.reading.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ExamRepository examRepository;
    private final ReadingPassageRepository readingPassageRepository;
    private final ReadingQuestionRepository readingQuestionRepository;
    private final UserAttemptRepository userAttemptRepository;
    private final ReadingAnswerRepository readingAnswerRepository;

    // IELTS Reading Band Score Conversion Table (40 questions)
    private static final int[][] BAND_SCORE_TABLE = {
        {39, 40, 90}, // 9.0
        {37, 38, 85}, // 8.5
        {35, 36, 80}, // 8.0
        {33, 34, 75}, // 7.5
        {30, 32, 70}, // 7.0
        {27, 29, 65}, // 6.5
        {23, 26, 60}, // 6.0
        {20, 22, 55}, // 5.5
        {15, 19, 50}, // 5.0
        {13, 14, 45}, // 4.5
        {10, 12, 40}, // 4.0
        {6, 9, 35},   // 3.5
        {4, 5, 30},   // 3.0
        {0, 3, 0}     // 0-3 = 2.5 or below
    };

    public List<ReadingExamDTO> getReadingExams(Exam.BandLevel bandLevel, Exam.ExamType examType) {
        List<Exam> exams;
        
        if (bandLevel != null && examType != null) {
            exams = examRepository.findBySkillAndBandLevelAndTypeAndIsActiveTrue(Exam.Skill.READING, bandLevel, examType);
        } else if (bandLevel != null) {
            exams = examRepository.findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill.READING, bandLevel);
        } else if (examType != null) {
            exams = examRepository.findBySkillAndTypeAndIsActiveTrue(Exam.Skill.READING, examType);
        } else {
            exams = examRepository.findBySkillAndIsActiveTrue(Exam.Skill.READING);
        }

        return exams.stream()
                .map(this::convertToExamDTO)
                .collect(Collectors.toList());
    }

    public ReadingExamDTO getReadingExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        if (exam.getSkill() != Exam.Skill.READING) {
            throw new BadRequestException("Not a reading exam");
        }

        return convertToExamDTOWithDetails(exam);
    }

    @Transactional
    public UserAttempt startAttempt(User user, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        if (exam.getSkill() != Exam.Skill.READING) {
            throw new BadRequestException("Not a reading exam");
        }

        // Count total questions
        List<ReadingPassage> passages = readingPassageRepository.findByExamOrderByPassageNumber(exam);
        int totalQuestions = passages.stream()
                .mapToInt(passage -> readingQuestionRepository.findByPassageOrderByOrderNumber(passage).size())
                .sum();

        UserAttempt attempt = UserAttempt.builder()
                .user(user)
                .exam(exam)
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(totalQuestions)
                .build();

        return userAttemptRepository.save(attempt);
    }

    @Transactional
    public ReadingResultDTO submitAttempt(User user, ReadingSubmitRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        // Create or get attempt
        List<ReadingPassage> passages = readingPassageRepository.findByExamOrderByPassageNumber(exam);
        int totalQuestions = passages.stream()
                .mapToInt(passage -> readingQuestionRepository.findByPassageOrderByOrderNumber(passage).size())
                .sum();

        UserAttempt attempt = UserAttempt.builder()
                .user(user)
                .exam(exam)
                .status(UserAttempt.AttemptStatus.SUBMITTED)
                .totalQuestions(totalQuestions)
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .submittedAt(LocalDateTime.now())
                .build();

        attempt = userAttemptRepository.save(attempt);

        // Build question map for quick lookup
        Map<Long, ReadingQuestion> questionMap = passages.stream()
                .flatMap(passage -> readingQuestionRepository.findByPassageOrderByOrderNumber(passage).stream())
                .collect(Collectors.toMap(ReadingQuestion::getId, q -> q));

        // Grade answers
        int correctCount = 0;
        List<ReadingAnswer> answers = new ArrayList<>();
        List<ReadingAnswerResultDTO> answerResults = new ArrayList<>();

        for (ReadingAnswerRequest answerReq : request.getAnswers()) {
            ReadingQuestion question = questionMap.get(answerReq.getQuestionId());
            if (question == null) continue;

            String userAnswer = normalizeAnswer(answerReq.getAnswer());
            String correctAnswer = normalizeAnswer(question.getCorrectAnswer());
            boolean isCorrect = checkAnswer(userAnswer, correctAnswer, question.getQuestionType());

            if (isCorrect) correctCount++;

            ReadingAnswer answer = ReadingAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .userAnswer(answerReq.getAnswer())
                    .isCorrect(isCorrect)
                    .build();
            answers.add(answer);

            answerResults.add(ReadingAnswerResultDTO.builder()
                    .questionId(question.getId())
                    .orderNumber(question.getOrderNumber())
                    .userAnswer(answerReq.getAnswer())
                    .correctAnswer(question.getCorrectAnswer())
                    .isCorrect(isCorrect)
                    .questionText(question.getQuestionText())
                    .explanation(question.getExplanation())
                    .build());
        }

        readingAnswerRepository.saveAll(answers);

        // Calculate band score
        BigDecimal bandScore = calculateBandScore(correctCount, totalQuestions);

        // Update attempt
        attempt.setCorrectAnswers(correctCount);
        attempt.setBandScore(bandScore);
        attempt.setStatus(UserAttempt.AttemptStatus.GRADED);
        userAttemptRepository.save(attempt);

        return ReadingResultDTO.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .totalQuestions(totalQuestions)
                .correctAnswers(correctCount)
                .bandScore(bandScore)
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .submittedAt(attempt.getSubmittedAt())
                .answerResults(answerResults)
                .build();
    }

    public ReadingResultDTO getAttemptResult(User user, Long attemptId) {
        UserAttempt attempt = userAttemptRepository.findByIdAndUser(attemptId, user)
                .orElseThrow(() -> new BadRequestException("Attempt not found"));

        if (attempt.getExam().getSkill() != Exam.Skill.READING) {
            throw new BadRequestException("Not a reading attempt");
        }

        List<ReadingAnswer> answers = readingAnswerRepository.findByAttemptOrderByQuestionOrderNumber(attempt);
        List<ReadingAnswerResultDTO> answerResults = answers.stream()
                .map(a -> ReadingAnswerResultDTO.builder()
                        .questionId(a.getQuestion().getId())
                        .orderNumber(a.getQuestion().getOrderNumber())
                        .userAnswer(a.getUserAnswer())
                        .correctAnswer(a.getQuestion().getCorrectAnswer())
                        .isCorrect(a.getIsCorrect())
                        .questionText(a.getQuestion().getQuestionText())
                        .explanation(a.getQuestion().getExplanation())
                        .build())
                .collect(Collectors.toList());

        return ReadingResultDTO.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExam().getId())
                .examTitle(attempt.getExam().getTitle())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .bandScore(attempt.getBandScore())
                .timeSpentSeconds(attempt.getTimeSpentSeconds())
                .submittedAt(attempt.getSubmittedAt())
                .answerResults(answerResults)
                .build();
    }

    public Page<UserAttempt> getUserAttempts(User user, Pageable pageable) {
        return userAttemptRepository.findByUserAndExamSkill(user, Exam.Skill.READING, pageable);
    }

    // Helper methods
    private ReadingExamDTO convertToExamDTO(Exam exam) {
        List<ReadingPassage> passages = readingPassageRepository.findByExamOrderByPassageNumber(exam);
        int totalQuestions = passages.stream()
                .mapToInt(passage -> readingQuestionRepository.findByPassageOrderByOrderNumber(passage).size())
                .sum();

        return ReadingExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(totalQuestions)
                .build();
    }

    private ReadingExamDTO convertToExamDTOWithDetails(Exam exam) {
        List<ReadingPassage> passages = readingPassageRepository.findByExamOrderByPassageNumber(exam);
        List<ReadingPassageDTO> passageDTOs = passages.stream()
                .map(passage -> {
                    List<ReadingQuestion> questions = readingQuestionRepository.findByPassageOrderByOrderNumber(passage);
                    List<ReadingQuestionDTO> questionDTOs = questions.stream()
                            .map(q -> ReadingQuestionDTO.builder()
                                    .id(q.getId())
                                    .orderNumber(q.getOrderNumber())
                                    .questionType(q.getQuestionType())
                                    .questionText(q.getQuestionText())
                                    .options(String.join("|", q.getOptions()))
                                    .points(1)
                                    .build())
                            .collect(Collectors.toList());

                    return ReadingPassageDTO.builder()
                            .id(passage.getId())
                            .passageNumber(passage.getPassageNumber())
                            .title(passage.getTitle())
                            .passageText(passage.getPassageText())
                            .wordCount(passage.getWordCount())
                            .questions(questionDTOs)
                            .build();
                })
                .collect(Collectors.toList());

        int totalQuestions = passageDTOs.stream()
                .mapToInt(p -> p.getQuestions().size())
                .sum();

        return ReadingExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(totalQuestions)
                .passages(passageDTOs)
                .build();
    }

    private String normalizeAnswer(String answer) {
        if (answer == null) return "";
        return answer.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private boolean checkAnswer(String userAnswer, String correctAnswer, ReadingQuestion.QuestionType type) {
        if (userAnswer.isEmpty()) return false;

        // For TRUE/FALSE/NOT GIVEN and YES/NO/NOT GIVEN questions
        if (type == ReadingQuestion.QuestionType.TRUE_FALSE_NOT_GIVEN ||
            type == ReadingQuestion.QuestionType.YES_NO_NOT_GIVEN) {
            return userAnswer.equalsIgnoreCase(correctAnswer);
        }

        // For multiple choice, exact match
        if (type == ReadingQuestion.QuestionType.MULTIPLE_CHOICE ||
            type == ReadingQuestion.QuestionType.MATCHING_HEADINGS ||
            type == ReadingQuestion.QuestionType.MATCHING_INFORMATION ||
            type == ReadingQuestion.QuestionType.MATCHING_FEATURES) {
            return userAnswer.equals(correctAnswer);
        }

        // For fill in the blank and completion types, allow some flexibility
        // Check if answers match (allowing for multiple correct answers separated by |)
        String[] acceptableAnswers = correctAnswer.split("\\|");
        for (String acceptable : acceptableAnswers) {
            if (normalizeAnswer(acceptable).equals(userAnswer)) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal calculateBandScore(int correctCount, int totalQuestions) {
        // Normalize to 40 questions scale (standard IELTS reading)
        int normalizedCorrect = totalQuestions == 40 ? correctCount : 
                (int) Math.round((double) correctCount / totalQuestions * 40);

        for (int[] range : BAND_SCORE_TABLE) {
            if (normalizedCorrect >= range[0] && normalizedCorrect <= range[1]) {
                return BigDecimal.valueOf(range[2]).divide(BigDecimal.TEN, 1, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.valueOf(2.5);
    }
}
