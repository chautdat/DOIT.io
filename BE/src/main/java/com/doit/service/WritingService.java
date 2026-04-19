package com.doit.service;

import com.doit.dto.writing.*;
import com.doit.entity.*;
import com.doit.exception.BadRequestException;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WritingService {

    private final ExamRepository examRepository;
    private final WritingTaskRepository writingTaskRepository;
    private final UserAttemptRepository userAttemptRepository;
    private final WritingSubmissionRepository writingSubmissionRepository;

    @Value("${openai.enabled:false}")
    private boolean openAiEnabled;

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

        return exams.stream()
                .map(this::convertToExamDTO)
                .collect(Collectors.toList());
    }

    public WritingExamDTO getWritingExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        if (exam.getSkill() != Exam.Skill.WRITING) {
            throw new BadRequestException("Not a writing exam");
        }

        return convertToExamDTOWithDetails(exam);
    }

    @Transactional
    public UserAttempt startAttempt(User user, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        if (exam.getSkill() != Exam.Skill.WRITING) {
            throw new BadRequestException("Not a writing exam");
        }

        // Count total tasks (usually 2 for IELTS Writing)
        List<WritingTask> tasks = writingTaskRepository.findByExamIdOrderByTaskNumber(examId);
        int totalTasks = tasks.size();

        UserAttempt attempt = UserAttempt.builder()
                .user(user)
                .exam(exam)
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .totalQuestions(totalTasks)
                .build();

        return userAttemptRepository.save(attempt);
    }

    @Transactional
    public WritingResultDTO submitEssay(User user, WritingSubmitRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        WritingTask task = writingTaskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new BadRequestException("Task not found"));

        if (!task.getExam().getId().equals(exam.getId())) {
            throw new BadRequestException("Task does not belong to this exam");
        }

        // Find or create attempt
        UserAttempt attempt = userAttemptRepository.findTopByUserAndExamAndStatusOrderByCreatedAtDesc(
                user, exam, UserAttempt.AttemptStatus.IN_PROGRESS
        ).orElseGet(() -> {
            List<WritingTask> tasks = writingTaskRepository.findByExamIdOrderByTaskNumber(exam.getId());
            return userAttemptRepository.save(UserAttempt.builder()
                    .user(user)
                    .exam(exam)
                    .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                    .totalQuestions(tasks.size())
                    .build());
        });

        // Count words
        int wordCount = countWords(request.getEssay());

        // Create submission
        WritingSubmission submission = WritingSubmission.builder()
                .attempt(attempt)
                .task(task)
                .userEssay(request.getEssay())
                .wordCount(wordCount)
                .build();

        submission = writingSubmissionRepository.save(submission);

        // Grade the essay using AI
        WritingResultDTO result = gradeEssayWithAI(submission, task);

        // Update submission with scores
        submission.setBandScore(result.getBandScore());
        submission.setTaskResponseScore(result.getTaskResponseScore());
        submission.setCoherenceCohesionScore(result.getCoherenceCohesionScore());
        submission.setLexicalResourceScore(result.getLexicalResourceScore());
        submission.setGrammarAccuracyScore(result.getGrammarAccuracyScore());
        submission.setAiFeedback(result.getAiFeedback());
        submission.setAiModel(result.getAiModel());
        submission.setGradedAt(LocalDateTime.now());
        writingSubmissionRepository.save(submission);

        // Update attempt if all tasks submitted
        updateAttemptStatus(attempt);

        return result;
    }

    public WritingResultDTO getSubmissionResult(User user, Long submissionId) {
        WritingSubmission submission = writingSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new BadRequestException("Submission not found"));

        if (!submission.getAttempt().getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Access denied");
        }

        return convertToResultDTO(submission);
    }

    public Page<WritingSubmission> getUserSubmissions(User user, Pageable pageable) {
        return writingSubmissionRepository.findByAttemptUserOrderBySubmittedAtDesc(user, pageable);
    }

    // Helper methods
    private WritingExamDTO convertToExamDTO(Exam exam) {
        List<WritingTask> tasks = writingTaskRepository.findByExamIdOrderByTaskNumber(exam.getId());

        return WritingExamDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .bandLevel(exam.getBandLevel())
                .examType(exam.getType())
                .durationMinutes(exam.getDurationMinutes())
                .tasks(tasks.stream()
                        .map(this::convertToTaskDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private WritingExamDTO convertToExamDTOWithDetails(Exam exam) {
        return convertToExamDTO(exam);
    }

    private WritingTaskDTO convertToTaskDTO(WritingTask task) {
        return WritingTaskDTO.builder()
                .id(task.getId())
                .taskNumber(task.getTaskNumber())
                .taskType(task.getTaskType())
                .promptText(task.getPromptText())
                .imageUrl(task.getImageUrl())
                .minWords(task.getMinWords())
                .tips(task.getTips())
                .build();
    }

    private WritingResultDTO convertToResultDTO(WritingSubmission submission) {
        WritingTask task = submission.getTask();
        Exam exam = submission.getAttempt().getExam();

        return WritingResultDTO.builder()
                .submissionId(submission.getId())
                .attemptId(submission.getAttempt().getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .taskId(task.getId())
                .taskNumber(task.getTaskNumber())
                .taskType(task.getTaskType())
                .promptText(task.getPromptText())
                .userEssay(submission.getUserEssay())
                .wordCount(submission.getWordCount())
                .timeSpentSeconds(submission.getAttempt().getTimeSpentSeconds())
                .submittedAt(submission.getSubmittedAt())
                .bandScore(submission.getBandScore())
                .taskResponseScore(submission.getTaskResponseScore())
                .coherenceCohesionScore(submission.getCoherenceCohesionScore())
                .lexicalResourceScore(submission.getLexicalResourceScore())
                .grammarAccuracyScore(submission.getGrammarAccuracyScore())
                .aiFeedback(submission.getAiFeedback())
                .aiModel(submission.getAiModel())
                .sampleAnswer(task.getSampleAnswer())
                .build();
    }

    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    private WritingResultDTO gradeEssayWithAI(WritingSubmission submission, WritingTask task) {
        if (openAiEnabled) {
            // TODO: Implement actual OpenAI API call
            return gradeWithOpenAI(submission, task);
        } else {
            // Use rule-based grading as fallback
            return gradeWithRules(submission, task);
        }
    }

    private WritingResultDTO gradeWithOpenAI(WritingSubmission submission, WritingTask task) {
        // TODO: Implement OpenAI API integration
        // For now, return rule-based grading
        return gradeWithRules(submission, task);
    }

    private WritingResultDTO gradeWithRules(WritingSubmission submission, WritingTask task) {
        String essay = submission.getUserEssay();
        int wordCount = submission.getWordCount();
        int minWords = task.getMinWords();

        // Basic scoring based on word count and length
        BigDecimal taskResponseScore = calculateTaskResponseScore(essay, wordCount, minWords, task);
        BigDecimal coherenceCohesionScore = calculateCoherenceCohesionScore(essay);
        BigDecimal lexicalResourceScore = calculateLexicalResourceScore(essay);
        BigDecimal grammarAccuracyScore = calculateGrammarAccuracyScore(essay);

        // Calculate overall band score (average of 4 criteria)
        BigDecimal totalScore = taskResponseScore.add(coherenceCohesionScore)
                .add(lexicalResourceScore).add(grammarAccuracyScore);
        BigDecimal bandScore = totalScore.divide(BigDecimal.valueOf(4), 1, RoundingMode.HALF_UP);

        // Generate feedback
        String feedback = generateFeedback(wordCount, minWords, taskResponseScore, 
                coherenceCohesionScore, lexicalResourceScore, grammarAccuracyScore);

        return WritingResultDTO.builder()
                .submissionId(submission.getId())
                .attemptId(submission.getAttempt().getId())
                .examId(submission.getAttempt().getExam().getId())
                .examTitle(submission.getAttempt().getExam().getTitle())
                .taskId(task.getId())
                .taskNumber(task.getTaskNumber())
                .taskType(task.getTaskType())
                .promptText(task.getPromptText())
                .userEssay(submission.getUserEssay())
                .wordCount(wordCount)
                .submittedAt(submission.getSubmittedAt())
                .bandScore(bandScore)
                .taskResponseScore(taskResponseScore)
                .coherenceCohesionScore(coherenceCohesionScore)
                .lexicalResourceScore(lexicalResourceScore)
                .grammarAccuracyScore(grammarAccuracyScore)
                .aiFeedback(feedback)
                .aiModel("rule-based")
                .sampleAnswer(task.getSampleAnswer())
                .build();
    }

    private BigDecimal calculateTaskResponseScore(String essay, int wordCount, int minWords, WritingTask task) {
        // Base score
        double score = 5.0;

        // Word count penalty/bonus
        if (wordCount < minWords * 0.7) {
            score -= 2.0; // Severe penalty for very short essays
        } else if (wordCount < minWords) {
            score -= 1.0; // Penalty for not meeting minimum
        } else if (wordCount >= minWords * 1.2) {
            score += 0.5; // Small bonus for longer essays
        }

        // Cap at 9.0
        return BigDecimal.valueOf(Math.min(Math.max(score, 1.0), 9.0))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCoherenceCohesionScore(String essay) {
        double score = 5.0;

        // Check for paragraph structure
        String[] paragraphs = essay.split("\n\n+");
        if (paragraphs.length >= 3) {
            score += 1.0;
        } else if (paragraphs.length >= 2) {
            score += 0.5;
        }

        // Check for linking words
        String[] linkingWords = {"however", "therefore", "furthermore", "moreover", "in addition", 
                "on the other hand", "consequently", "as a result", "in conclusion", "firstly", 
                "secondly", "finally"};
        int linkingCount = 0;
        String lowerEssay = essay.toLowerCase();
        for (String word : linkingWords) {
            if (lowerEssay.contains(word)) {
                linkingCount++;
            }
        }
        score += Math.min(linkingCount * 0.3, 1.5);

        return BigDecimal.valueOf(Math.min(Math.max(score, 1.0), 9.0))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLexicalResourceScore(String essay) {
        double score = 5.0;

        // Calculate unique words ratio
        String[] words = essay.toLowerCase().split("\\s+");
        long uniqueWords = java.util.Arrays.stream(words)
                .distinct()
                .count();
        
        double uniqueRatio = (double) uniqueWords / words.length;
        if (uniqueRatio > 0.7) {
            score += 1.5;
        } else if (uniqueRatio > 0.5) {
            score += 0.5;
        }

        // Check for advanced vocabulary (simplified)
        String[] advancedWords = {"consequently", "nevertheless", "substantial", "significant", 
                "predominantly", "comprehensive", "crucial", "fundamental", "perspective"};
        int advancedCount = 0;
        String lowerEssay = essay.toLowerCase();
        for (String word : advancedWords) {
            if (lowerEssay.contains(word)) {
                advancedCount++;
            }
        }
        score += Math.min(advancedCount * 0.3, 1.0);

        return BigDecimal.valueOf(Math.min(Math.max(score, 1.0), 9.0))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateGrammarAccuracyScore(String essay) {
        double score = 6.0;

        // Basic grammar checks (simplified)
        // Check sentence structure
        String[] sentences = essay.split("[.!?]+");
        int validSentences = 0;
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 10 && Character.isUpperCase(trimmed.charAt(0))) {
                validSentences++;
            }
        }
        double sentenceRatio = (double) validSentences / sentences.length;
        if (sentenceRatio > 0.8) {
            score += 1.0;
        } else if (sentenceRatio < 0.5) {
            score -= 1.0;
        }

        return BigDecimal.valueOf(Math.min(Math.max(score, 1.0), 9.0))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private String generateFeedback(int wordCount, int minWords, BigDecimal taskResponse,
                                    BigDecimal coherence, BigDecimal lexical, BigDecimal grammar) {
        StringBuilder feedback = new StringBuilder();
        
        feedback.append("**Writing Assessment Feedback**\n\n");

        // Word count feedback
        if (wordCount < minWords) {
            feedback.append("⚠️ **Word Count**: Your essay has ").append(wordCount)
                    .append(" words. The minimum requirement is ").append(minWords)
                    .append(" words. Please expand your ideas.\n\n");
        } else {
            feedback.append("✅ **Word Count**: Good! Your essay meets the word count requirement (")
                    .append(wordCount).append(" words).\n\n");
        }

        // Task Response
        feedback.append("**Task Response** (").append(taskResponse).append("/9): ");
        if (taskResponse.compareTo(BigDecimal.valueOf(6)) >= 0) {
            feedback.append("You addressed the main points of the task adequately.\n\n");
        } else {
            feedback.append("Try to address all parts of the task more fully.\n\n");
        }

        // Coherence & Cohesion
        feedback.append("**Coherence & Cohesion** (").append(coherence).append("/9): ");
        if (coherence.compareTo(BigDecimal.valueOf(6)) >= 0) {
            feedback.append("Good paragraph organization and use of linking devices.\n\n");
        } else {
            feedback.append("Consider using more transition words and organizing your ideas into clear paragraphs.\n\n");
        }

        // Lexical Resource
        feedback.append("**Lexical Resource** (").append(lexical).append("/9): ");
        if (lexical.compareTo(BigDecimal.valueOf(6)) >= 0) {
            feedback.append("Good vocabulary range demonstrated.\n\n");
        } else {
            feedback.append("Try to use more varied and topic-specific vocabulary.\n\n");
        }

        // Grammar
        feedback.append("**Grammatical Range & Accuracy** (").append(grammar).append("/9): ");
        if (grammar.compareTo(BigDecimal.valueOf(6)) >= 0) {
            feedback.append("Good grammatical control with a variety of sentence structures.\n\n");
        } else {
            feedback.append("Focus on sentence variety and accuracy. Check for common errors.\n\n");
        }

        return feedback.toString();
    }

    private void updateAttemptStatus(UserAttempt attempt) {
        List<WritingTask> tasks = writingTaskRepository.findByExamIdOrderByTaskNumber(attempt.getExam().getId());
        List<WritingSubmission> submissions = writingSubmissionRepository.findByAttemptOrderByTaskTaskNumber(attempt);

        if (submissions.size() >= tasks.size()) {
            // All tasks submitted, calculate average band score
            BigDecimal totalBand = submissions.stream()
                    .map(WritingSubmission::getBandScore)
                    .filter(b -> b != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgBand = totalBand.divide(BigDecimal.valueOf(submissions.size()), 1, RoundingMode.HALF_UP);
            
            attempt.setBandScore(avgBand);
            attempt.setCorrectAnswers(submissions.size());
            attempt.setStatus(UserAttempt.AttemptStatus.GRADED);
            attempt.setSubmittedAt(LocalDateTime.now());
            userAttemptRepository.save(attempt);
        }
    }
}
