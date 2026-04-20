package com.doit.service;

import com.doit.dto.writing.*;
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
public class WritingService {

    private final ExamRepository examRepository;
    private final WritingTaskRepository writingTaskRepository;
    private final WritingSubmissionRepository writingSubmissionRepository;
    private final UserAttemptRepository userAttemptRepository;

    public List<WritingExamDTO> getWritingExams(Exam.BandLevel bandLevel, Exam.ExamType examType) {
        List<Exam> exams;
        if (bandLevel != null && examType != null) {
            exams = examRepository.findBySkillAndBandLevelAndTypeAndIsActiveTrue(Exam.Skill.WRITING, bandLevel, examType);
        } else if (bandLevel != null) {
            exams = examRepository.findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill.WRITING, bandLevel);
        } else if (examType != null) {
            exams = examRepository.findBySkillAndTypeAndIsActiveTrue(Exam.Skill.WRITING, examType);
        } else {
            exams = examRepository.findBySkillAndIsActiveTrue(Exam.Skill.WRITING);
        }
        return exams.stream().map(this::convertToExamDTO).collect(Collectors.toList());
    }

    public WritingExamDTO getWritingExam(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        return convertToExamDTOWithDetails(exam);
    }

    public UserAttempt startAttempt(User user, String examId) {
        examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));
        List<WritingTask> tasks = writingTaskRepository.findByExamId(examId);

        UserAttempt attempt = UserAttempt.builder()
                .userId(user.getId())
                .examId(examId)
                .skill("WRITING")
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(tasks.size())
                .startedAt(LocalDateTime.now())
                .build();

        return userAttemptRepository.save(attempt);
    }

    public WritingResultDTO submitAttempt(User user, WritingSubmitRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        UserAttempt attempt = UserAttempt.builder()
                .userId(user.getId())
                .examId(exam.getId())
                .skill("WRITING")
                .status(UserAttempt.AttemptStatus.SUBMITTED)
                .totalQuestions(1)
                .timeSpentSeconds(request.getTimeSpentSeconds())
                .startedAt(LocalDateTime.now())
                .submittedAt(LocalDateTime.now())
                .build();
        attempt = userAttemptRepository.save(attempt);

        WritingSubmission submission = WritingSubmission.builder()
                .attemptId(attempt.getId())
                .userId(user.getId())
                .taskId(request.getTaskId())
                .submittedText(request.getEssay())
                .wordCount(countWords(request.getEssay()))
                .status(WritingSubmission.SubmissionStatus.SUBMITTED)
                .build();
        writingSubmissionRepository.save(submission);

        return WritingResultDTO.builder()
                .submissionId(submission.getId())
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .taskId(request.getTaskId())
                .content(request.getEssay())
                .wordCount(submission.getWordCount())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }

    public WritingResultDTO getAttemptResult(User user, String attemptId) {
        UserAttempt attempt = userAttemptRepository.findByIdAndUserId(attemptId, user.getId())
                .orElseThrow(() -> new BadRequestException("Attempt not found"));
        List<WritingSubmission> submissions = writingSubmissionRepository.findByAttemptId(attemptId);
        if (submissions.isEmpty()) {
            throw new BadRequestException("No submission found");
        }
        WritingSubmission submission = submissions.get(0);
        return WritingResultDTO.builder()
                .submissionId(submission.getId())
                .attemptId(attempt.getId())
                .examId(attempt.getExamId())
                .taskId(submission.getTaskId())
                .content(submission.getSubmittedText())
                .wordCount(submission.getWordCount())
                .submittedAt(submission.getSubmittedAt())
                .bandScore(submission.getOverallBandScore())
                .build();
    }

    public Page<UserAttempt> getUserAttempts(User user, Pageable pageable) {
        return userAttemptRepository.findByUserIdAndSkill(user.getId(), "WRITING", pageable);
    }

    private WritingExamDTO convertToExamDTO(Exam exam) {
        List<WritingTask> tasks = writingTaskRepository.findByExamId(exam.getId());
        return WritingExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalTasks(tasks.size())
                .build();
    }

    private WritingExamDTO convertToExamDTOWithDetails(Exam exam) {
        List<WritingTask> tasks = writingTaskRepository.findByExamId(exam.getId());
        List<WritingTaskDTO> taskDTOs = tasks.stream()
                .map(t -> WritingTaskDTO.builder()
                        .id(t.getId())
                        .taskType(t.getTaskType())
                        .promptText(t.getTaskDescription())
                        .imageUrl(t.getImageUrl())
                        .minWords(t.getMinWords())
                        .build())
                .collect(Collectors.toList());
        return WritingExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .totalTasks(tasks.size())
                .tasks(taskDTOs)
                .build();
    }

    private int countWords(String content) {
        if (content == null || content.trim().isEmpty()) return 0;
        return content.trim().split("\\s+").length;
    }
}
