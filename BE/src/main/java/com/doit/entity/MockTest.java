package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listening_attempt_id")
    private UserAttempt listeningAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_attempt_id")
    private UserAttempt readingAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writing_attempt_id")
    private UserAttempt writingAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speaking_attempt_id")
    private UserAttempt speakingAttempt;

    @Column(name = "listening_band", precision = 2, scale = 1)
    private BigDecimal listeningBand;

    @Column(name = "reading_band", precision = 2, scale = 1)
    private BigDecimal readingBand;

    @Column(name = "writing_band", precision = 2, scale = 1)
    private BigDecimal writingBand;

    @Column(name = "speaking_band", precision = 2, scale = 1)
    private BigDecimal speakingBand;

    @Column(name = "overall_band", precision = 2, scale = 1)
    private BigDecimal overallBand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MockTestStatus status = MockTestStatus.IN_PROGRESS;

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum MockTestStatus {
        IN_PROGRESS,
        LISTENING,
        READING,
        WRITING,
        SPEAKING,
        COMPLETED,
        GRADED
    }
}
