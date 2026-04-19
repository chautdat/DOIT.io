package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "speaking_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private UserAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private SpeakingPart part;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "band_score", precision = 2, scale = 1)
    private BigDecimal bandScore;

    // IELTS Speaking criteria scores
    @Column(name = "fluency_coherence_score", precision = 2, scale = 1)
    private BigDecimal fluencyCoherenceScore;

    @Column(name = "lexical_resource_score", precision = 2, scale = 1)
    private BigDecimal lexicalResourceScore;

    @Column(name = "grammar_accuracy_score", precision = 2, scale = 1)
    private BigDecimal grammarAccuracyScore;

    @Column(name = "pronunciation_score", precision = 2, scale = 1)
    private BigDecimal pronunciationScore;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "ai_model")
    private String aiModel;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
}
