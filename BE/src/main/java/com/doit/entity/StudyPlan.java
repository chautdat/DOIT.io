package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "current_band", precision = 2, scale = 1)
    private BigDecimal currentBand;

    @Column(name = "target_band", precision = 2, scale = 1, nullable = false)
    private BigDecimal targetBand;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "focus_listening")
    @Builder.Default
    private Boolean focusListening = true;

    @Column(name = "focus_reading")
    @Builder.Default
    private Boolean focusReading = true;

    @Column(name = "focus_writing")
    @Builder.Default
    private Boolean focusWriting = true;

    @Column(name = "focus_speaking")
    @Builder.Default
    private Boolean focusSpeaking = true;

    @Column(name = "study_hours_per_day")
    @Builder.Default
    private Integer studyHoursPerDay = 2;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudyPlanItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
