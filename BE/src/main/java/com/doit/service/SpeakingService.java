package com.doit.service;

import com.doit.dto.speaking.*;
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
public class SpeakingService {

    private final ExamRepository examRepository;
    private final SpeakingPartRepository speakingPartRepository;
    private final SpeakingSubmissionRepository speakingSubmissionRepository;
    private final UserAttemptRepository userAttemptRepository;

    public List<SpeakingExamDTO> getSpeakingExams(Exam.BandLevel bandLevel, Exam.ExamType examType) {
        List<Exam> exams;
        if (bandLevel != null && examType != null) {
            exams = examRepository.findBySkillAndBandLevelAndTypeAndIsActiveTrue(Exam.Skill.SPEAKING, bandLevel, examType);
        } else if (bandLevel != null) {
            exams = examRepository.findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill.SPEAKING, bandLevel);
        } else if (examType != null) {
            exams = examRepository.findBySkillAndTypeAndIsActiveTrue(Exam.Skill.SPEAKING, examType);
        } else {
            exams = examRepository.findBySkillAndIsActiveTrue(Exam.Skill.SPEAKING);
        }
        return exams.stream().map(this::convertToExamDTO).collect(Collectors.toList());
    }

    public SpeakingExamDTO getSpeakingExam(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        return convertToExamDTOWithDetails(exam);
    }

    public UserAttempt startAttempt(User user, String examId) {
        examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        List<SpeakingPart> parts = speakingPartRepository.findByExamId(examId);

        UserAttempt attempt = UserAttempt.builder()
                .userId(user.getId())
                .examId(examId)
                .skill("SPEAKING")
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(parts.size())
                .startedAt(LocalDateTime.now())
                .build();

        return userAttemptRepository.save(attempt);
    }

    public SpeakingResultDTO submitAttempt(User user, SpeakingSubmitRequest request) {
        UserAttempt attempt = userAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new BadRequestException("Attempt not found"));
        
        if (!attempt.getUserId().equals(user.getId())) {
            throw new BadRequestException("Attempt does not belong to user");
        }

        List<SpeakingSubmitRequest.SpeakingPartAnswer> partAnswers = request.getPartAnswers();
        for (SpeakingSubmitRequest.SpeakingPartAnswer answer : partAnswers) {
            SpeakingSubmission submission = SpeakingSubmission.builder()
                    .attemptId(attempt.getId())
                    .userId(user.getId())
                    .partId(answer.getPartId())
                    .audioUrl(answer.getAudioUrl())
                    .transcript(answer.getTranscript())
                    .durationSeconds(answer.getDurationSeconds())
                    .status(SpeakingSubmission.SubmissionStatus.SUBMITTED)
                    .build();
            speakingSubmissionRepository.save(submission);
        }

        attempt.setStatus(UserAttempt.AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());
        userAttemptRepository.save(attempt);

        return SpeakingResultDTO.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExamId())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }

    public SpeakingResultDTO getAttemptResult(User user, String attemptId) {
        UserAttempt attempt = userAttemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new BadRequestException("Attempt not found"));
        List<SpeakingSubmission> submissions = speakingSubmissionRepository.findByAttemptId(attemptId);
        
        Double avgBand = submissions.stream()
                .filter(s -> s.getOverallBandScore() != null)
                .mapToDouble(SpeakingSubmission::getOverallBandScore)
                .average().orElse(0.0);
        
        return SpeakingResultDTO.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExamId())
                .bandScore(avgBand)
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }

    public Page<UserAttempt> getUserAttempts(User user, Pageable pageable) {
        return userAttemptRepository.findByUserIdAndSkill(user.getId(), "SPEAKING", pageable);
    }

    private SpeakingExamDTO convertToExamDTO(Exam exam) {
        List<SpeakingPart> parts = speakingPartRepository.findByExamId(exam.getId());
        return SpeakingExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalParts(parts.size())
                .build();
    }

    private SpeakingExamDTO convertToExamDTOWithDetails(Exam exam) {
        List<SpeakingPart> parts = speakingPartRepository.findByExamId(exam.getId());
        List<SpeakingPartDTO> partDTOs = parts.stream()
                .map(p -> SpeakingPartDTO.builder()
                        .id(p.getId())
                        .partNumber(p.getPartType() != null ? p.getPartType().ordinal() + 1 : null)
                        .topic(p.getTopic())
                        .questions(p.getQuestions())
                        .build())
                .collect(Collectors.toList());
        return SpeakingExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalParts(parts.size())
                .parts(partDTOs)
                .build();
    }
}
