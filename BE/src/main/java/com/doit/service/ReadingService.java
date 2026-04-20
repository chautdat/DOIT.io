package com.doit.service;

import com.doit.dto.reading.*;
import com.doit.entity.*;
import com.doit.exception.BadRequestException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ExamRepository examRepository;
    private final ReadingPassageRepository readingPassageRepository;
    private final ReadingQuestionRepository readingQuestionRepository;
    private final UserAttemptRepository userAttemptRepository;
    private final ReadingAnswerRepository readingAnswerRepository;

    private static final int[][] BAND_SCORE_TABLE = {
        {39, 40, 90}, {37, 38, 85}, {35, 36, 80}, {33, 34, 75},
        {30, 32, 70}, {27, 29, 65}, {23, 26, 60}, {20, 22, 55},
        {16, 19, 50}, {13, 15, 45}, {10, 12, 40}, {6, 9, 35},
        {4, 5, 30}, {0, 3, 0}
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
        return exams.stream().map(this::convertToExamDTO).collect(Collectors.toList());
    }

    public ReadingExamDTO getReadingExam(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        if (exam.getSkill() != Exam.Skill.READING) {
            throw new BadRequestException("Not a reading exam");
        }
        return convertToExamDTOWithDetails(exam);
    }

    public UserAttempt startAttempt(User user, String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        List<ReadingPassage> passages = readingPassageRepository.findByExamIdOrderByPassageNumber(examId);
        int totalQuestions = passages.stream()
                .mapToInt(p -> readingQuestionRepository.findByPassageIdOrderByOrderNumber(p.getId()).size())
                .sum();

        UserAttempt attempt = UserAttempt.builder()
                .userId(user.getId())
                .examId(examId)
                .skill("READING")
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(totalQuestions)
                .startedAt(LocalDateTime.now())
                .build();

        return userAttemptRepository.save(attempt);
    }

    public ReadingResultDTO submitAttempt(User user, ReadingSubmitRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        List<ReadingPassage> passages = readingPassageRepository.findByExamIdOrderByPassageNumber(exam.getId());
        int totalQuestions = passages.stream()
                .mapToInt(p -> readingQuestionRepository.findByPassageIdOrderByOrderNumber(p.getId()).size())
                .sum();

        UserAttempt attempt = UserAttempt.builder()
                .userId(user.getId())
                .examId(exam.getId())
                .skill("READING")
                .status(UserAttempt.AttemptStatus.SUBMITTED)
                .totalQuestions(totalQuestions)
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .startedAt(LocalDateTime.now())
                .submittedAt(LocalDateTime.now())
                .build();
        attempt = userAttemptRepository.save(attempt);

        Map<String, ReadingQuestion> questionMap = passages.stream()
                .flatMap(p -> readingQuestionRepository.findByPassageIdOrderByOrderNumber(p.getId()).stream())
                .collect(Collectors.toMap(ReadingQuestion::getId, q -> q));

        int correctCount = 0;
        List<ReadingAnswer> answers = new ArrayList<>();

        for (ReadingAnswerRequest answerReq : request.getAnswers()) {
            ReadingQuestion question = questionMap.get(answerReq.getQuestionId());
            if (question == null) continue;

            String userAnswer = normalizeAnswer(answerReq.getAnswer());
            String correctAnswer = normalizeAnswer(question.getCorrectAnswer());
            boolean isCorrect = userAnswer.equals(correctAnswer);
            if (isCorrect) correctCount++;

            answers.add(ReadingAnswer.builder()
                    .attemptId(attempt.getId())
                    .questionId(question.getId())
                    .userAnswer(answerReq.getAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        readingAnswerRepository.saveAll(answers);

        double bandScore = calculateBandScore(correctCount, totalQuestions);
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
                .build();
    }

    public ReadingResultDTO getAttemptResult(User user, String attemptId) {
        UserAttempt attempt = userAttemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new BadRequestException("Attempt not found"));
        Exam exam = examRepository.findById(attempt.getExamId()).orElse(null);
        return ReadingResultDTO.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExamId())
                .examTitle(exam != null ? exam.getTitle() : "")
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .bandScore(attempt.getBandScore())
                .timeSpentSeconds(attempt.getTimeSpentSeconds())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }

    public Page<UserAttempt> getUserAttempts(User user, Pageable pageable) {
        return userAttemptRepository.findByUserIdAndSkill(user.getId(), "READING", pageable);
    }

    private ReadingExamDTO convertToExamDTO(Exam exam) {
        List<ReadingPassage> passages = readingPassageRepository.findByExamIdOrderByPassageNumber(exam.getId());
        int totalQuestions = passages.stream()
                .mapToInt(p -> readingQuestionRepository.findByPassageIdOrderByOrderNumber(p.getId()).size())
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
        List<ReadingPassage> passages = readingPassageRepository.findByExamIdOrderByPassageNumber(exam.getId());
        List<ReadingPassageDTO> passageDTOs = passages.stream()
                .map(p -> {
                    List<ReadingQuestion> questions = readingQuestionRepository.findByPassageIdOrderByOrderNumber(p.getId());
                    List<ReadingQuestionDTO> questionDTOs = questions.stream()
                            .map(q -> ReadingQuestionDTO.builder()
                                    .id(q.getId())
                                    .orderNumber(q.getOrderNumber())
                                    .questionType(q.getQuestionType())
                                    .questionText(q.getQuestionText())
                                    .options(q.getOptions() != null ? String.join("|", q.getOptions()) : "")
                                    .build())
                            .collect(Collectors.toList());
                    return ReadingPassageDTO.builder()
                            .id(p.getId())
                            .passageNumber(p.getPassageNumber())
                            .title(p.getTitle())
                            .passageText(p.getPassageText())
                            .questions(questionDTOs)
                            .build();
                })
                .collect(Collectors.toList());
        int totalQuestions = passageDTOs.stream().mapToInt(p -> p.getQuestions().size()).sum();
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
        return answer.trim().toLowerCase();
    }

    private double calculateBandScore(int correctCount, int totalQuestions) {
        int normalizedCorrect = totalQuestions == 40 ? correctCount : 
                (int) Math.round((double) correctCount / totalQuestions * 40);
        for (int[] range : BAND_SCORE_TABLE) {
            if (normalizedCorrect >= range[0] && normalizedCorrect <= range[1]) {
                return range[2] / 10.0;
            }
        }
        return 2.5;
    }
}
