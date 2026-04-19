package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_attempts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "band_score", precision = 2, scale = 1)
    private BigDecimal bandScore;

    @Column(name = "is_mock_test")
    @Builder.Default
    private Boolean isMockTest = false;

    @Column(name = "mock_test_id")
    private Long mockTestId;

    // Listening answers
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ListeningAnswer> listeningAnswers = new ArrayList<>();

    // Reading answers
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReadingAnswer> readingAnswers = new ArrayList<>();

    // Writing submissions
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WritingSubmission> writingSubmissions = new ArrayList<>();

    // Speaking submissions
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SpeakingSubmission> speakingSubmissions = new ArrayList<>();

    public enum AttemptStatus {
        IN_PROGRESS,
        SUBMITTED,
        GRADED,
        EXPIRED
    }
}
