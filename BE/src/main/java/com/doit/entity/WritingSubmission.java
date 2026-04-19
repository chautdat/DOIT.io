package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "writing_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private UserAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private WritingTask task;

    @Column(name = "user_essay", columnDefinition = "TEXT", nullable = false)
    private String userEssay;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "band_score", precision = 2, scale = 1)
    private BigDecimal bandScore;

    // IELTS Writing criteria scores
    @Column(name = "task_response_score", precision = 2, scale = 1)
    private BigDecimal taskResponseScore;

    @Column(name = "coherence_cohesion_score", precision = 2, scale = 1)
    private BigDecimal coherenceCohesionScore;

    @Column(name = "lexical_resource_score", precision = 2, scale = 1)
    private BigDecimal lexicalResourceScore;

    @Column(name = "grammar_accuracy_score", precision = 2, scale = 1)
    private BigDecimal grammarAccuracyScore;

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
