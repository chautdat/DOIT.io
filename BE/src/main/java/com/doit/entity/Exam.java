package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "band_level", nullable = false)
    private BandLevel bandLevel;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_free")
    @Builder.Default
    private Boolean isFree = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ExamType {
        PRACTICE,       // Luyện tập từng skill
        PLACEMENT,      // Test đầu vào
        MOCK_TEST       // Thi thử full
    }

    public enum Skill {
        LISTENING,
        READING,
        WRITING,
        SPEAKING
    }

    public enum BandLevel {
        BEGINNER,       // 0.0 - 4.5
        ELEMENTARY,     // 5.0 - 6.0
        INTERMEDIATE,   // 6.5 - 7.0
        ADVANCED        // 7.5 - 9.0
    }
}
