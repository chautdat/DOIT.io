package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_skill_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "skill"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Exam.Skill skill;

    @Column(name = "current_band", precision = 2, scale = 1)
    private BigDecimal currentBand;

    @Column(name = "highest_band", precision = 2, scale = 1)
    private BigDecimal highestBand;

    @Column(name = "attempts_count")
    @Builder.Default
    private Integer attemptsCount = 0;

    @Column(name = "total_practice_time_minutes")
    @Builder.Default
    private Integer totalPracticeTimeMinutes = 0;

    @Column(name = "average_accuracy")
    private BigDecimal averageAccuracy;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
