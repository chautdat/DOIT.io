package com.doit.service;

import com.doit.dto.listening.*;
import com.doit.entity.*;
import com.doit.exception.BadRequestException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private static final int[][] BAND_SCORE_TABLE = {
        {39, 40, 90}, {37, 38, 85}, {35, 36, 80}, {33, 34, 75},
        {30, 32, 70}, {27, 29, 65}, {23, 26, 60}, {20, 22, 55},
        {16, 19, 50}, {13, 15, 45}, {10, 12, 40}, {6, 9, 35},
        {4, 5, 30}, {0, 3, 0}
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
        return exams.stream().map(this::convertToExamDTO).collect(Collectors.toList());
    }

    public ListeningExamDTO getListeningExam(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        if (exam.getSkill() != Exam.Skill.LISTENING) {
            throw new BadRequestException("Not a listening exam");
        }
        return convertToExamDTOWithDetails(exam);
    }

    public UserAttempt startAttempt(User user, String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamIdOrderByPartNumber(examId);
        int totalQuestions = audioParts.stream()
                .mapToInt(audio -> listeningQuestionRepository.findByAudioIdOrderByOrderNumber(audio.getId()).size())
                .sum();

        UserAttempt attempt = UserAttempt.builder()
                .userId(user.getId())
                .examId(examId)
                .skill("LISTENING")
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(totalQuestions)
                .startedAt(LocalDateTime.now())
                .build();

        return userAttemptRepository.save(attempt);
    }

    public ListeningResultDTO submitAttempt(User user, ListeningSubmitRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamIdOrderByPartNumber(exam.getId());
        int totalQuestions = audioParts.stream()
                .mapToInt(audio -> listeningQuestionRepository.findByAudioIdOrderByOrderNumber(audio.getId()).size())
                .sum();

        UserAttempt attempt = UserAttempt.builder()
                .userId(user.getId())
                .examId(exam.getId())
                .skill("LISTENING")
                .status(UserAttempt.AttemptStatus.SUBMITTED)
                .totalQuestions(totalQuestions)
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .startedAt(LocalDateTime.now())
                .submittedAt(LocalDateTime.now())
                .build();
        attempt = userAttemptRepository.save(attempt);

        Map<String, ListeningQuestion> questionMap = audioParts.stream()
                .flatMap(audio -> listeningQuestionRepository.findByAudioIdOrderByOrderNumber(audio.getId()).stream())
                .collect(Collectors.toMap(ListeningQuestion::getId, q -> q));

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
                    .attemptId(attempt.getId())
                    .questionId(question.getId())
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

        double bandScore = calculateBandScore(correctCount, totalQuestions);

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

    public ListeningResultDTO getAttemptResult(User user, String attemptId) {
        UserAttempt attempt = userAttemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new BadRequestException("Attempt not found"));

        Exam exam = examRepository.findById(attempt.getExamId())
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        List<ListeningAnswer> answers = listeningAnswerRepository.findByAttemptId(attemptId);
        List<ListeningAnswerResultDTO> answerResults = answers.stream()
                .map(a -> {
                    ListeningQuestion question = listeningQuestionRepository.findById(a.getQuestionId()).orElse(null);
                    return ListeningAnswerResultDTO.builder()
                            .questionId(a.getQuestionId())
                            .orderNumber(question != null ? question.getOrderNumber() : 0)
                            .userAnswer(a.getUserAnswer())
                            .correctAnswer(question != null ? question.getCorrectAnswer() : "")
                            .isCorrect(a.getIsCorrect())
                            .questionText(question != null ? question.getQuestionText() : "")
                            .build();
                })
                .collect(Collectors.toList());

        return ListeningResultDTO.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .bandScore(attempt.getBandScore())
                .timeSpentSeconds(attempt.getTimeSpentSeconds())
                .submittedAt(attempt.getSubmittedAt())
                .answerResults(answerResults)
                .build();
    }

    public Page<UserAttempt> getUserAttempts(User user, Pageable pageable) {
        return userAttemptRepository.findByUserIdAndSkill(user.getId(), "LISTENING", pageable);
    }

    private ListeningExamDTO convertToExamDTO(Exam exam) {
        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamIdOrderByPartNumber(exam.getId());
        int totalQuestions = audioParts.stream()
                .mapToInt(audio -> listeningQuestionRepository.findByAudioIdOrderByOrderNumber(audio.getId()).size())
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
        List<ListeningAudio> audioParts = listeningAudioRepository.findByExamIdOrderByPartNumber(exam.getId());
        List<ListeningAudioDTO> audioDTOs = audioParts.stream()
                .map(audio -> {
                    List<ListeningQuestion> questions = listeningQuestionRepository.findByAudioIdOrderByOrderNumber(audio.getId());
                    List<ListeningQuestionDTO> questionDTOs = questions.stream()
                            .map(q -> ListeningQuestionDTO.builder()
                                    .id(q.getId())
                                    .orderNumber(q.getOrderNumber())
                                    .questionType(q.getQuestionType())
                                    .questionText(q.getQuestionText())
                                    .options(q.getOptions() != null ? String.join("|", q.getOptions()) : "")
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

        int totalQuestions = audioDTOs.stream().mapToInt(a -> a.getQuestions().size()).sum();

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
        if (type == ListeningQuestion.QuestionType.MULTIPLE_CHOICE) {
            return userAnswer.equals(correctAnswer);
        }
        String[] acceptableAnswers = correctAnswer.split("\\|");
        for (String acceptable : acceptableAnswers) {
            if (normalizeAnswer(acceptable).equals(userAnswer)) {
                return true;
            }
        }
        return false;
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
