package com.doit.service;

import com.doit.dto.listening.*;
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
public class ListeningService {

    private final ExamRepository examRepository;
    private final ListeningAudioRepository listeningAudioRepository;
    private final ListeningQuestionRepository listeningQuestionRepository;
    private final UserAttemptRepository userAttemptRepository;
    private final ListeningAnswerRepository listeningAnswerRepository;

    // IELTS Band Score Conversion Table (40 questions)
    private static final int[][] BAND_SCORE_TABLE = {
        {39, 40, 90}, // 9.0
        {37, 38, 85}, // 8.5
        {35, 36, 80}, // 8.0
        {33, 34, 75}, // 7.5
        {30, 32, 70}, // 7.0
        {27, 29, 65}, // 6.5
        {23, 26, 60}, // 6.0
        {20, 22, 55}, // 5.5
        {16, 19, 50}, // 5.0
        {13, 15, 45}, // 4.5
        {10, 12, 40}, // 4.0
        {6, 9, 35},   // 3.5
        {4, 5, 30},   // 3.0
        {0, 3, 0}     // 0-3 = 2.5 or below
    };

    public List<ListeningExamDTO> getListeningExams(Exam.BandLevel bandLevel, Exam.ExamType examType) {
        List<Exam> exams;
        
        if (bandLevel != null && examType != null) {
            exams = examRepository.findBySkillAndBandLevelAndTypeAndIsActiveTrue(Exam.Skill.LISTENING, bandLevel, examType);
        } else if (bandLevel != null) {
            exams = examRepository.findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill.LISTENING, bandLevel);
        } else if (examType != null) {
            exams = examRepository.findBySkillAndTypeAndIsActiveTrue(Exam.Skill.LISTENING, examType);
        } else {
            exams = examRepository.findBySkillAndIsActiveTrue(Exam.Skill.LISTENING);
        }

        return exams.stream()
                .map(this::convertToExamDTO)
                .collect(Collectors.toList());
    }

    public ListeningExamDTO getListeningExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        if (exam.getSkill() != Exam.Skill.LISTENING) {
            throw new BadRequestException("Not a listening exam");
        }

        return convertToExamDTOWithDetails(exam);
    }

    @Transactional
    public UserAttempt startAttempt(User user, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        if (exam.getSkill() != Exam.Skill.LISTENING) {
            throw new BadRequestException("Not a listening exam");
        }

        // Count total questions
        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamOrderByPartNumber(exam);
        int totalQuestions = audioParts.stream()
                .mapToInt(audio -> listeningQuestionRepository.findByAudioOrderByOrderNumber(audio).size())
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
    public ListeningResultDTO submitAttempt(User user, ListeningSubmitRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        // Create attempt
        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamOrderByPartNumber(exam);
        int totalQuestions = audioParts.stream()
                .mapToInt(audio -> listeningQuestionRepository.findByAudioOrderByOrderNumber(audio).size())
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
        Map<Long, ListeningQuestion> questionMap = audioParts.stream()
                .flatMap(audio -> listeningQuestionRepository.findByAudioOrderByOrderNumber(audio).stream())
                .collect(Collectors.toMap(ListeningQuestion::getId, q -> q));

        // Grade answers
        int correctCount = 0;
        List<ListeningAnswer> answers = new ArrayList<>();
        List<ListeningAnswerResultDTO> answerResults = new ArrayList<>();

        for (ListeningAnswerRequest answerReq : request.getAnswers()) {
            ListeningQuestion question = questionMap.get(answerReq.getQuestionId());
            if (question == null) continue;

            String userAnswer = normalizeAnswer(answerReq.getAnswer());
            String correctAnswer = normalizeAnswer(question.getCorrectAnswer());
            boolean isCorrect = checkAnswer(userAnswer, correctAnswer, question.getQuestionType());

            if (isCorrect) correctCount++;

            ListeningAnswer answer = ListeningAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .userAnswer(answerReq.getAnswer())
                    .isCorrect(isCorrect)
                    .build();
            answers.add(answer);

            answerResults.add(ListeningAnswerResultDTO.builder()
                    .questionId(question.getId())
                    .orderNumber(question.getOrderNumber())
                    .userAnswer(answerReq.getAnswer())
                    .correctAnswer(question.getCorrectAnswer())
                    .isCorrect(isCorrect)
                    .questionText(question.getQuestionText())
                    .build());
        }

        listeningAnswerRepository.saveAll(answers);

        // Calculate band score
        BigDecimal bandScore = calculateBandScore(correctCount, totalQuestions);

        // Update attempt
        attempt.setCorrectAnswers(correctCount);
        attempt.setBandScore(bandScore);
        attempt.setStatus(UserAttempt.AttemptStatus.GRADED);
        userAttemptRepository.save(attempt);

        return ListeningResultDTO.builder()
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

    public ListeningResultDTO getAttemptResult(User user, Long attemptId) {
        UserAttempt attempt = userAttemptRepository.findByIdAndUser(attemptId, user)
                .orElseThrow(() -> new BadRequestException("Attempt not found"));

        if (attempt.getExam().getSkill() != Exam.Skill.LISTENING) {
            throw new BadRequestException("Not a listening attempt");
        }

        List<ListeningAnswer> answers = listeningAnswerRepository.findByAttemptOrderByQuestionOrderNumber(attempt);
        List<ListeningAnswerResultDTO> answerResults = answers.stream()
                .map(a -> ListeningAnswerResultDTO.builder()
                        .questionId(a.getQuestion().getId())
                        .orderNumber(a.getQuestion().getOrderNumber())
                        .userAnswer(a.getUserAnswer())
                        .correctAnswer(a.getQuestion().getCorrectAnswer())
                        .isCorrect(a.getIsCorrect())
                        .questionText(a.getQuestion().getQuestionText())
                        .build())
                .collect(Collectors.toList());

        return ListeningResultDTO.builder()
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
        return userAttemptRepository.findByUserAndExamSkill(user, Exam.Skill.LISTENING, pageable);
    }

    // Helper methods
    private ListeningExamDTO convertToExamDTO(Exam exam) {
        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamOrderByPartNumber(exam);
        int totalQuestions = audioParts.stream()
                .mapToInt(audio -> listeningQuestionRepository.findByAudioOrderByOrderNumber(audio).size())
                .sum();

        return ListeningExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(totalQuestions)
                .build();
    }

    private ListeningExamDTO convertToExamDTOWithDetails(Exam exam) {
        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamOrderByPartNumber(exam);
        List<ListeningAudioDTO> audioDTOs = audioParts.stream()
                .map(audio -> {
                    List<ListeningQuestion> questions = listeningQuestionRepository.findByAudioOrderByOrderNumber(audio);
                    List<ListeningQuestionDTO> questionDTOs = questions.stream()
                            .map(q -> ListeningQuestionDTO.builder()
                                    .id(q.getId())
                                    .orderNumber(q.getOrderNumber())
                                    .questionType(q.getQuestionType())
                                    .questionText(q.getQuestionText())
                                    .options(String.join("|", q.getOptions()))
                                    .points(1)
                                    .build())
                            .collect(Collectors.toList());

                    return ListeningAudioDTO.builder()
                            .id(audio.getId())
                            .partNumber(audio.getPartNumber())
                            .title(audio.getPartTitle())
                            .audioUrl(audio.getAudioUrl())
                            .durationSeconds(audio.getDurationSeconds())
                            .questions(questionDTOs)
                            .build();
                })
                .collect(Collectors.toList());

        int totalQuestions = audioDTOs.stream()
                .mapToInt(a -> a.getQuestions().size())
                .sum();

        return ListeningExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(totalQuestions)
                .audioParts(audioDTOs)
                .build();
    }

    private String normalizeAnswer(String answer) {
        if (answer == null) return "";
        return answer.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private boolean checkAnswer(String userAnswer, String correctAnswer, ListeningQuestion.QuestionType type) {
        if (userAnswer.isEmpty()) return false;

        // For multiple choice, exact match
        if (type == ListeningQuestion.QuestionType.MULTIPLE_CHOICE) {
            return userAnswer.equals(correctAnswer);
        }

        // For fill in the blank, allow some flexibility
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
        // Normalize to 40 questions scale (standard IELTS listening)
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
