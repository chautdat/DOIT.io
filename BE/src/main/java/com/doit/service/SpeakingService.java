package com.doit.service;

import com.doit.dto.speaking.*;
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
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpeakingService {

    private final ExamRepository examRepository;
    private final SpeakingPartRepository speakingPartRepository;
    private final SpeakingSubmissionRepository speakingSubmissionRepository;
    private final UserAttemptRepository userAttemptRepository;
    private final UserRepository userRepository;

    @Value("${whisper.enabled:false}")
    private boolean whisperEnabled;

    // IELTS Speaking Band Score Descriptors
    private static final Map<Integer, String> BAND_DESCRIPTIONS = Map.of(
            9, "Expert user - Fluent, accurate, and sophisticated",
            8, "Very good user - Occasional inaccuracies but handles complex language",
            7, "Good user - Operational command with occasional errors",
            6, "Competent user - Generally effective despite some inaccuracies",
            5, "Modest user - Partial command with frequent errors",
            4, "Limited user - Basic competence limited to familiar situations",
            3, "Extremely limited user - Conveys only general meaning"
    );

    // Common linking words for coherence analysis
    private static final Set<String> LINKING_WORDS = Set.of(
            "firstly", "secondly", "thirdly", "finally", "however", "moreover", "furthermore",
            "additionally", "nevertheless", "consequently", "therefore", "thus", "hence",
            "although", "though", "despite", "whereas", "while", "meanwhile", "subsequently",
            "in addition", "on the other hand", "in contrast", "as a result", "for example",
            "for instance", "in conclusion", "to summarize", "overall", "basically", "actually",
            "obviously", "clearly", "apparently", "generally", "usually", "normally", "typically"
    );

    // Advanced vocabulary for lexical resource scoring
    private static final Set<String> ADVANCED_VOCABULARY = Set.of(
            "phenomenon", "perspective", "significant", "substantial", "fundamental",
            "comprehensive", "sophisticated", "contemporary", "inevitable", "crucial",
            "essential", "remarkable", "considerable", "extensive", "prevalent",
            "predominant", "compelling", "intriguing", "fascinating", "versatile",
            "innovative", "sustainable", "influential", "prominent", "distinctive"
    );

    /**
     * Get all Speaking exams with optional filtering
     */
    @Transactional(readOnly = true)
    public Page<SpeakingExamDTO> getSpeakingExams(Exam.ExamType type, Exam.BandLevel bandLevel, Pageable pageable) {
        Page<Exam> exams;

        if (bandLevel != null) {
            exams = examRepository.findBySkillAndBandLevelAndIsActiveTrue(Exam.Skill.SPEAKING, bandLevel, pageable);
        } else {
            exams = examRepository.findBySkillAndIsActiveTrue(Exam.Skill.SPEAKING, pageable);
        }

        return exams.map(this::convertToSpeakingExamDTO);
    }

    /**
     * Get Speaking exam by ID with all parts
     */
    @Transactional(readOnly = true)
    public SpeakingExamDTO getSpeakingExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found with id: " + examId));

        if (exam.getSkill() != Exam.Skill.SPEAKING) {
            throw new IllegalArgumentException("Exam is not a Speaking exam");
        }

        return convertToSpeakingExamDTO(exam);
    }

    /**
     * Start a new Speaking attempt
     */
    public Long startAttempt(Long examId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userId));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BadRequestException("Exam not found with id: " + examId));

        if (exam.getSkill() != Exam.Skill.SPEAKING) {
            throw new IllegalArgumentException("Exam is not a Speaking exam");
        }

        // Check for existing in-progress attempt
        Optional<UserAttempt> existingAttempt = userAttemptRepository
                .findTopByUserAndExamAndStatusOrderByCreatedAtDesc(user, exam, UserAttempt.AttemptStatus.IN_PROGRESS);

        if (existingAttempt.isPresent()) {
            return existingAttempt.get().getId();
        }

        // Create new attempt
        UserAttempt attempt = UserAttempt.builder()
                .user(user)
                .exam(exam)
                .status(UserAttempt.AttemptStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();

        attempt = userAttemptRepository.save(attempt);
        log.info("Started new Speaking attempt {} for user {} on exam {}", attempt.getId(), userId, examId);

        return attempt.getId();
    }

    /**
     * Submit Speaking responses for grading
     */
    public SpeakingResultDTO submitAttempt(SpeakingSubmitRequest request, Long userId) {
        UserAttempt attempt = userAttemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new BadRequestException("Attempt not found with id: " + request.getAttemptId()));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Attempt does not belong to this user");
        }

        if (attempt.getStatus() == UserAttempt.AttemptStatus.SUBMITTED) {
            throw new IllegalArgumentException("Attempt has already been completed");
        }

        Exam exam = attempt.getExam();
        List<SpeakingPart> parts = speakingPartRepository.findByExamIdOrderByPartNumber(exam.getId());
        
        List<SpeakingSubmission> submissions = new ArrayList<>();
        BigDecimal totalFluency = BigDecimal.ZERO;
        BigDecimal totalLexical = BigDecimal.ZERO;
        BigDecimal totalGrammar = BigDecimal.ZERO;
        BigDecimal totalPronunciation = BigDecimal.ZERO;
        int totalDuration = 0;
        int partsGraded = 0;

        // Process each part answer
        for (SpeakingSubmitRequest.SpeakingPartAnswer partAnswer : request.getPartAnswers()) {
            SpeakingPart part = parts.stream()
                    .filter(p -> p.getId().equals(partAnswer.getPartId()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Speaking part not found: " + partAnswer.getPartId()));

            // Get or transcribe the transcript
            String transcript = partAnswer.getTranscript();
            if (transcript == null || transcript.isBlank()) {
                // If no transcript provided, would use Whisper for transcription
                if (whisperEnabled && partAnswer.getAudioUrl() != null) {
                    transcript = transcribeAudio(partAnswer.getAudioUrl());
                } else {
                    transcript = "";
                }
            }

            // Grade the response
            SpeakingGradingResult gradingResult = gradeResponse(transcript, part);

            SpeakingSubmission submission = SpeakingSubmission.builder()
                    .attempt(attempt)
                    .part(part)
                    .audioUrl(partAnswer.getAudioUrl())
                    .transcript(transcript)
                    .durationSeconds(partAnswer.getDurationSeconds())
                    .bandScore(gradingResult.bandScore)
                    .fluencyCoherenceScore(gradingResult.fluencyCoherenceScore)
                    .lexicalResourceScore(gradingResult.lexicalResourceScore)
                    .grammarAccuracyScore(gradingResult.grammarAccuracyScore)
                    .pronunciationScore(gradingResult.pronunciationScore)
                    .aiFeedback(gradingResult.feedback)
                    .aiModel("rule-based-v1")
                    .gradedAt(LocalDateTime.now())
                    .build();

            submissions.add(speakingSubmissionRepository.save(submission));

            if (!transcript.isBlank()) {
                totalFluency = totalFluency.add(gradingResult.fluencyCoherenceScore);
                totalLexical = totalLexical.add(gradingResult.lexicalResourceScore);
                totalGrammar = totalGrammar.add(gradingResult.grammarAccuracyScore);
                totalPronunciation = totalPronunciation.add(gradingResult.pronunciationScore);
                partsGraded++;
            }

            if (partAnswer.getDurationSeconds() != null) {
                totalDuration += partAnswer.getDurationSeconds();
            }
        }

        // Calculate average scores
        BigDecimal avgFluency = partsGraded > 0 
                ? totalFluency.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgLexical = partsGraded > 0 
                ? totalLexical.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgGrammar = partsGraded > 0 
                ? totalGrammar.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgPronunciation = partsGraded > 0 
                ? totalPronunciation.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Calculate overall band score
        BigDecimal overallScore = calculateOverallBandScore(avgFluency, avgLexical, avgGrammar, avgPronunciation);

        // Update attempt
        attempt.setStatus(UserAttempt.AttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setBandScore(overallScore);
        attempt.setTimeSpentSeconds(totalDuration);
        userAttemptRepository.save(attempt);

        log.info("Completed Speaking attempt {} with overall score {}", attempt.getId(), overallScore);

        return buildResultDTO(attempt, exam, submissions, overallScore, avgFluency, avgLexical, avgGrammar, avgPronunciation, totalDuration);
    }

    /**
     * Get Speaking attempt result
     */
    @Transactional(readOnly = true)
    public SpeakingResultDTO getAttemptResult(Long attemptId, Long userId) {
        UserAttempt attempt = userAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new BadRequestException("Attempt not found with id: " + attemptId));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Attempt does not belong to this user");
        }

        Exam exam = attempt.getExam();
        List<SpeakingSubmission> submissions = speakingSubmissionRepository.findByAttemptOrderByPartPartNumberAsc(attempt);

        // Calculate averages from submissions
        BigDecimal totalFluency = BigDecimal.ZERO;
        BigDecimal totalLexical = BigDecimal.ZERO;
        BigDecimal totalGrammar = BigDecimal.ZERO;
        BigDecimal totalPronunciation = BigDecimal.ZERO;
        int totalDuration = 0;
        int partsGraded = 0;

        for (SpeakingSubmission submission : submissions) {
            if (submission.getBandScore() != null && submission.getBandScore().compareTo(BigDecimal.ZERO) > 0) {
                totalFluency = totalFluency.add(submission.getFluencyCoherenceScore() != null ? submission.getFluencyCoherenceScore() : BigDecimal.ZERO);
                totalLexical = totalLexical.add(submission.getLexicalResourceScore() != null ? submission.getLexicalResourceScore() : BigDecimal.ZERO);
                totalGrammar = totalGrammar.add(submission.getGrammarAccuracyScore() != null ? submission.getGrammarAccuracyScore() : BigDecimal.ZERO);
                totalPronunciation = totalPronunciation.add(submission.getPronunciationScore() != null ? submission.getPronunciationScore() : BigDecimal.ZERO);
                partsGraded++;
            }
            if (submission.getDurationSeconds() != null) {
                totalDuration += submission.getDurationSeconds();
            }
        }

        BigDecimal avgFluency = partsGraded > 0 
                ? totalFluency.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgLexical = partsGraded > 0 
                ? totalLexical.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgGrammar = partsGraded > 0 
                ? totalGrammar.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgPronunciation = partsGraded > 0 
                ? totalPronunciation.divide(BigDecimal.valueOf(partsGraded), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return buildResultDTO(attempt, exam, submissions, attempt.getBandScore(), avgFluency, avgLexical, avgGrammar, avgPronunciation, totalDuration);
    }

    // ==================== Private Helper Methods ====================

    private SpeakingExamDTO convertToSpeakingExamDTO(Exam exam) {
        List<SpeakingPart> parts = speakingPartRepository.findByExamIdOrderByPartNumber(exam.getId());

        List<SpeakingPartDTO> partDTOs = parts.stream()
                .map(this::convertToSpeakingPartDTO)
                .collect(Collectors.toList());

        return SpeakingExamDTO.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .type(exam.getType())
                .bandLevel(exam.getBandLevel())
                .durationMinutes(exam.getDurationMinutes())
                .totalParts(partDTOs.size())
                .parts(partDTOs)
                .build();
    }

    private SpeakingPartDTO convertToSpeakingPartDTO(SpeakingPart part) {
        return SpeakingPartDTO.builder()
                .id(part.getId())
                .partNumber(part.getPartNumber())
                .topic(part.getTopic())
                .topicDescription(part.getTopicDescription())
                .questions(part.getQuestions())
                .cueCard(part.getCueCard())
                .prepTimeSeconds(part.getPrepTimeSeconds())
                .speakTimeSeconds(part.getSpeakTimeSeconds())
                .tips(part.getTips())
                .build();
    }

    /**
     * Grade a Speaking response using rule-based analysis
     */
    private SpeakingGradingResult gradeResponse(String transcript, SpeakingPart part) {
        if (transcript == null || transcript.isBlank()) {
            return new SpeakingGradingResult(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    "No response provided."
            );
        }

        // Calculate individual criterion scores
        BigDecimal fluencyScore = calculateFluencyCoherenceScore(transcript, part);
        BigDecimal lexicalScore = calculateLexicalResourceScore(transcript);
        BigDecimal grammarScore = calculateGrammarAccuracyScore(transcript);
        BigDecimal pronunciationScore = calculatePronunciationScore(transcript);

        // Calculate overall band score
        BigDecimal bandScore = calculateOverallBandScore(fluencyScore, lexicalScore, grammarScore, pronunciationScore);

        // Generate feedback
        String feedback = generateFeedback(transcript, part, fluencyScore, lexicalScore, grammarScore, pronunciationScore, bandScore);

        return new SpeakingGradingResult(bandScore, fluencyScore, lexicalScore, grammarScore, pronunciationScore, feedback);
    }

    /**
     * Calculate Fluency and Coherence score
     */
    private BigDecimal calculateFluencyCoherenceScore(String transcript, SpeakingPart part) {
        String[] words = transcript.toLowerCase().split("\\s+");
        int wordCount = words.length;

        // Base score starts at 5
        double score = 5.0;

        // Word count analysis - check against expected speaking time
        int expectedMinWords = 0;
        if (part.getSpeakTimeSeconds() != null) {
            // Average speaking rate: ~120-150 words per minute
            expectedMinWords = (part.getSpeakTimeSeconds() / 60) * 100;
        }

        if (expectedMinWords > 0) {
            double wordRatio = (double) wordCount / expectedMinWords;
            if (wordRatio >= 0.8 && wordRatio <= 1.5) {
                score += 1.5; // Good length
            } else if (wordRatio >= 0.5) {
                score += 0.5; // Acceptable length
            } else {
                score -= 1.0; // Too short
            }
        }

        // Linking words usage
        long linkingWordCount = Arrays.stream(words)
                .filter(LINKING_WORDS::contains)
                .count();
        double linkingRatio = (double) linkingWordCount / Math.max(wordCount, 1);

        if (linkingRatio >= 0.05) {
            score += 1.0;
        } else if (linkingRatio >= 0.02) {
            score += 0.5;
        }

        // Sentence variety (check for different sentence starters)
        String[] sentences = transcript.split("[.!?]+");
        Set<String> sentenceStarters = new HashSet<>();
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                String[] sentenceWords = trimmed.split("\\s+");
                if (sentenceWords.length > 0) {
                    sentenceStarters.add(sentenceWords[0].toLowerCase());
                }
            }
        }
        if (sentenceStarters.size() >= 5) {
            score += 0.5;
        }

        // Check for hesitation markers (negative indicator)
        long hesitations = Arrays.stream(words)
                .filter(w -> w.matches("uh|um|er|ah|hmm|like"))
                .count();
        double hesitationRatio = (double) hesitations / Math.max(wordCount, 1);
        if (hesitationRatio > 0.05) {
            score -= 0.5;
        }

        return BigDecimal.valueOf(Math.min(9.0, Math.max(1.0, score))).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Calculate Lexical Resource score
     */
    private BigDecimal calculateLexicalResourceScore(String transcript) {
        String[] words = transcript.toLowerCase().replaceAll("[^a-zA-Z\\s]", "").split("\\s+");
        int wordCount = words.length;

        if (wordCount == 0) {
            return BigDecimal.valueOf(1.0);
        }

        // Base score
        double score = 5.0;

        // Vocabulary diversity (Type-Token Ratio)
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        double ttr = (double) uniqueWords.size() / wordCount;

        if (ttr >= 0.6) {
            score += 1.5;
        } else if (ttr >= 0.4) {
            score += 1.0;
        } else if (ttr >= 0.3) {
            score += 0.5;
        }

        // Advanced vocabulary usage
        long advancedCount = uniqueWords.stream()
                .filter(ADVANCED_VOCABULARY::contains)
                .count();

        if (advancedCount >= 5) {
            score += 1.5;
        } else if (advancedCount >= 3) {
            score += 1.0;
        } else if (advancedCount >= 1) {
            score += 0.5;
        }

        // Word length variety (indicator of sophisticated vocabulary)
        long longWords = Arrays.stream(words)
                .filter(w -> w.length() >= 8)
                .count();
        double longWordRatio = (double) longWords / wordCount;

        if (longWordRatio >= 0.15) {
            score += 0.5;
        }

        // Penalize excessive repetition
        Map<String, Long> wordFrequency = Arrays.stream(words)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
        long overusedWords = wordFrequency.values().stream()
                .filter(count -> count > wordCount * 0.05)
                .count();
        if (overusedWords > 3) {
            score -= 0.5;
        }

        return BigDecimal.valueOf(Math.min(9.0, Math.max(1.0, score))).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Calculate Grammatical Range and Accuracy score
     */
    private BigDecimal calculateGrammarAccuracyScore(String transcript) {
        String[] sentences = transcript.split("[.!?]+");
        int sentenceCount = sentences.length;

        if (sentenceCount == 0) {
            return BigDecimal.valueOf(1.0);
        }

        // Base score
        double score = 5.0;

        // Sentence length variety
        List<Integer> sentenceLengths = new ArrayList<>();
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                sentenceLengths.add(trimmed.split("\\s+").length);
            }
        }

        if (!sentenceLengths.isEmpty()) {
            double avgLength = sentenceLengths.stream().mapToInt(Integer::intValue).average().orElse(0);
            int minLength = sentenceLengths.stream().mapToInt(Integer::intValue).min().orElse(0);
            int maxLength = sentenceLengths.stream().mapToInt(Integer::intValue).max().orElse(0);

            // Good sentence length variety
            if (maxLength - minLength >= 10 && avgLength >= 8) {
                score += 1.5;
            } else if (maxLength - minLength >= 5 && avgLength >= 6) {
                score += 1.0;
            } else if (avgLength >= 5) {
                score += 0.5;
            }
        }

        // Complex sentence structures (check for subordinating conjunctions)
        Pattern complexPattern = Pattern.compile(
                "\\b(although|because|since|unless|while|whereas|if|when|after|before|until|as|that|which|who|whom|whose)\\b",
                Pattern.CASE_INSENSITIVE
        );
        long complexCount = Arrays.stream(sentences)
                .filter(s -> complexPattern.matcher(s).find())
                .count();
        double complexRatio = (double) complexCount / sentenceCount;

        if (complexRatio >= 0.4) {
            score += 1.0;
        } else if (complexRatio >= 0.2) {
            score += 0.5;
        }

        // Check for proper capitalization at sentence starts
        long properCapitalization = Arrays.stream(sentences)
                .map(String::trim)
                .filter(s -> !s.isEmpty() && Character.isUpperCase(s.charAt(0)))
                .count();
        double capRatio = (double) properCapitalization / sentenceCount;

        if (capRatio >= 0.8) {
            score += 0.5;
        }

        return BigDecimal.valueOf(Math.min(9.0, Math.max(1.0, score))).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Calculate Pronunciation score
     * Note: True pronunciation scoring requires audio analysis.
     * This is a placeholder using text-based heuristics.
     */
    private BigDecimal calculatePronunciationScore(String transcript) {
        // Base score - without actual audio analysis, we can only estimate
        double score = 6.0;

        String[] words = transcript.toLowerCase().split("\\s+");
        int wordCount = words.length;

        if (wordCount == 0) {
            return BigDecimal.valueOf(1.0);
        }

        // Check for phonetically complex words (longer words with varied letter patterns)
        // This is a rough proxy for pronunciation challenge
        long complexWords = Arrays.stream(words)
                .filter(w -> w.length() >= 10 || w.matches(".*[aeiou]{3,}.*") || w.matches(".*[^aeiou]{4,}.*"))
                .count();

        // Using complex words suggests confidence in pronunciation
        if (complexWords >= 5) {
            score += 1.0;
        } else if (complexWords >= 2) {
            score += 0.5;
        }

        // Varied word endings (suggests awareness of grammatical forms)
        Set<String> endings = new HashSet<>();
        for (String word : words) {
            if (word.length() >= 3) {
                endings.add(word.substring(word.length() - 3));
            }
        }
        if (endings.size() >= wordCount * 0.3) {
            score += 0.5;
        }

        return BigDecimal.valueOf(Math.min(9.0, Math.max(1.0, score))).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Calculate overall band score from criterion scores
     */
    private BigDecimal calculateOverallBandScore(BigDecimal fluency, BigDecimal lexical, BigDecimal grammar, BigDecimal pronunciation) {
        if (fluency.compareTo(BigDecimal.ZERO) == 0 && lexical.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // IELTS Speaking: Average of 4 criteria, rounded to nearest 0.5
        BigDecimal sum = fluency.add(lexical).add(grammar).add(pronunciation);
        BigDecimal average = sum.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);

        // Round to nearest 0.5
        double rawScore = average.doubleValue();
        double rounded = Math.round(rawScore * 2) / 2.0;

        return BigDecimal.valueOf(rounded).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Generate detailed feedback for the response
     */
    private String generateFeedback(String transcript, SpeakingPart part, 
            BigDecimal fluency, BigDecimal lexical, BigDecimal grammar, BigDecimal pronunciation, BigDecimal overall) {
        
        StringBuilder feedback = new StringBuilder();
        feedback.append("## Speaking Assessment - Part ").append(part.getPartNumber()).append("\n\n");
        feedback.append("**Topic:** ").append(part.getTopic()).append("\n\n");

        // Overall score
        int bandInt = overall.intValue();
        String bandDesc = BAND_DESCRIPTIONS.getOrDefault(bandInt, "Developing user");
        feedback.append("### Overall Band Score: ").append(overall).append("\n");
        feedback.append("*").append(bandDesc).append("*\n\n");

        // Criterion scores
        feedback.append("### Criterion Scores\n\n");
        feedback.append("| Criterion | Score |\n");
        feedback.append("|-----------|-------|\n");
        feedback.append("| Fluency and Coherence | ").append(fluency).append(" |\n");
        feedback.append("| Lexical Resource | ").append(lexical).append(" |\n");
        feedback.append("| Grammatical Range and Accuracy | ").append(grammar).append(" |\n");
        feedback.append("| Pronunciation | ").append(pronunciation).append(" |\n\n");

        // Detailed feedback
        feedback.append("### Detailed Feedback\n\n");

        // Fluency feedback
        feedback.append("**Fluency and Coherence:** ");
        if (fluency.compareTo(BigDecimal.valueOf(7)) >= 0) {
            feedback.append("Your response flows smoothly with good use of linking words and coherent ideas. ");
        } else if (fluency.compareTo(BigDecimal.valueOf(5)) >= 0) {
            feedback.append("Your response is generally fluent but could benefit from more linking words and varied sentence structures. ");
        } else {
            feedback.append("Try to speak more continuously and use more linking words to connect your ideas. ");
        }
        feedback.append("\n\n");

        // Lexical feedback
        feedback.append("**Lexical Resource:** ");
        if (lexical.compareTo(BigDecimal.valueOf(7)) >= 0) {
            feedback.append("Excellent vocabulary range with good use of sophisticated words and expressions. ");
        } else if (lexical.compareTo(BigDecimal.valueOf(5)) >= 0) {
            feedback.append("Good vocabulary but consider using more varied and advanced vocabulary to express your ideas. ");
        } else {
            feedback.append("Try to expand your vocabulary and avoid repeating the same words. Consider learning topic-specific vocabulary. ");
        }
        feedback.append("\n\n");

        // Grammar feedback
        feedback.append("**Grammatical Range and Accuracy:** ");
        if (grammar.compareTo(BigDecimal.valueOf(7)) >= 0) {
            feedback.append("Good control of complex sentence structures with mostly accurate grammar. ");
        } else if (grammar.compareTo(BigDecimal.valueOf(5)) >= 0) {
            feedback.append("Generally accurate grammar but try to use more complex sentence structures. ");
        } else {
            feedback.append("Focus on basic sentence structures and common tenses. Practice complex sentences gradually. ");
        }
        feedback.append("\n\n");

        // Pronunciation feedback
        feedback.append("**Pronunciation:** ");
        feedback.append("Based on text analysis, your response shows ");
        if (pronunciation.compareTo(BigDecimal.valueOf(7)) >= 0) {
            feedback.append("confidence with complex vocabulary, suggesting good pronunciation awareness. ");
        } else {
            feedback.append("room for improvement. Practice with phonetically challenging words. ");
        }
        feedback.append("*Note: For accurate pronunciation feedback, audio analysis is recommended.*\n\n");

        // Improvement tips
        feedback.append("### Tips for Improvement\n\n");
        if (overall.compareTo(BigDecimal.valueOf(7)) < 0) {
            if (fluency.compareTo(lexical) < 0) {
                feedback.append("- Practice speaking continuously for 1-2 minutes on various topics\n");
                feedback.append("- Use more linking words: firstly, however, moreover, in addition\n");
            }
            if (lexical.compareTo(grammar) < 0) {
                feedback.append("- Learn 5 new topic-related vocabulary words daily\n");
                feedback.append("- Use synonyms to avoid word repetition\n");
            }
            if (grammar.compareTo(fluency) < 0) {
                feedback.append("- Practice using complex sentences with 'because', 'although', 'while'\n");
                feedback.append("- Review tense consistency in your responses\n");
            }
        } else {
            feedback.append("- Continue practicing with a variety of topics\n");
            feedback.append("- Challenge yourself with more abstract and academic topics\n");
            feedback.append("- Record yourself and review your responses\n");
        }

        return feedback.toString();
    }

    /**
     * Transcribe audio using Whisper (placeholder for external integration)
     */
    private String transcribeAudio(String audioUrl) {
        // TODO: Integrate with OpenAI Whisper or similar speech-to-text service
        log.info("Whisper transcription requested for: {}", audioUrl);
        return "";
    }

    private SpeakingResultDTO buildResultDTO(UserAttempt attempt, Exam exam, List<SpeakingSubmission> submissions,
            BigDecimal overallScore, BigDecimal avgFluency, BigDecimal avgLexical, BigDecimal avgGrammar, 
            BigDecimal avgPronunciation, int totalDuration) {

        List<SpeakingResultDTO.PartResultDTO> partResults = submissions.stream()
                .map(submission -> SpeakingResultDTO.PartResultDTO.builder()
                        .partId(submission.getPart().getId())
                        .partNumber(submission.getPart().getPartNumber())
                        .topic(submission.getPart().getTopic())
                        .audioUrl(submission.getAudioUrl())
                        .transcript(submission.getTranscript())
                        .durationSeconds(submission.getDurationSeconds())
                        .bandScore(submission.getBandScore())
                        .fluencyCoherenceScore(submission.getFluencyCoherenceScore())
                        .lexicalResourceScore(submission.getLexicalResourceScore())
                        .grammarAccuracyScore(submission.getGrammarAccuracyScore())
                        .pronunciationScore(submission.getPronunciationScore())
                        .aiFeedback(submission.getAiFeedback())
                        .submittedAt(submission.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());

        // Generate overall feedback
        String overallFeedback = generateOverallFeedback(overallScore, avgFluency, avgLexical, avgGrammar, avgPronunciation);

        return SpeakingResultDTO.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .overallBandScore(overallScore)
                .fluencyCoherenceScore(avgFluency)
                .lexicalResourceScore(avgLexical)
                .grammarAccuracyScore(avgGrammar)
                .pronunciationScore(avgPronunciation)
                .totalDurationSeconds(totalDuration)
                .partsCompleted(partResults.size())
                .totalParts(speakingPartRepository.findByExamIdOrderByPartNumber(exam.getId()).size())
                .startedAt(attempt.getStartedAt())
                .completedAt(attempt.getSubmittedAt())
                .partResults(partResults)
                .overallFeedback(overallFeedback)
                .build();
    }

    private String generateOverallFeedback(BigDecimal overall, BigDecimal fluency, BigDecimal lexical, 
            BigDecimal grammar, BigDecimal pronunciation) {
        
        int bandInt = overall != null ? overall.intValue() : 0;
        String bandDesc = BAND_DESCRIPTIONS.getOrDefault(bandInt, "Developing user");
        
        StringBuilder feedback = new StringBuilder();
        feedback.append("## Overall Speaking Assessment\n\n");
        feedback.append("**Band Score: ").append(overall != null ? overall : "N/A").append("** - ").append(bandDesc).append("\n\n");
        
        // Find strongest and weakest areas
        Map<String, BigDecimal> scores = new LinkedHashMap<>();
        scores.put("Fluency & Coherence", fluency != null ? fluency : BigDecimal.ZERO);
        scores.put("Lexical Resource", lexical != null ? lexical : BigDecimal.ZERO);
        scores.put("Grammar", grammar != null ? grammar : BigDecimal.ZERO);
        scores.put("Pronunciation", pronunciation != null ? pronunciation : BigDecimal.ZERO);
        
        String strongest = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
        String weakest = scores.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
        
        feedback.append("**Strongest Area:** ").append(strongest).append("\n");
        feedback.append("**Area for Improvement:** ").append(weakest).append("\n\n");
        
        feedback.append("### Next Steps\n");
        feedback.append("- Review individual part feedback for detailed analysis\n");
        feedback.append("- Practice regularly with similar topics\n");
        feedback.append("- Record yourself and compare with model answers\n");
        
        return feedback.toString();
    }

    // Helper class for grading result
    private static class SpeakingGradingResult {
        final BigDecimal bandScore;
        final BigDecimal fluencyCoherenceScore;
        final BigDecimal lexicalResourceScore;
        final BigDecimal grammarAccuracyScore;
        final BigDecimal pronunciationScore;
        final String feedback;

        SpeakingGradingResult(BigDecimal bandScore, BigDecimal fluencyCoherenceScore, BigDecimal lexicalResourceScore,
                BigDecimal grammarAccuracyScore, BigDecimal pronunciationScore, String feedback) {
            this.bandScore = bandScore;
            this.fluencyCoherenceScore = fluencyCoherenceScore;
            this.lexicalResourceScore = lexicalResourceScore;
            this.grammarAccuracyScore = grammarAccuracyScore;
            this.pronunciationScore = pronunciationScore;
            this.feedback = feedback;
        }
    }
}
