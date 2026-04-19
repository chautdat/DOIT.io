package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "study_plan_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private StudyPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Exam.Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(name = "activity_type")
    private String activityType; // PRACTICE, REVIEW, MOCK_TEST

    @Column(name = "activity_description")
    private String activityDescription;

    @Column(name = "recommended_date", nullable = false)
    private LocalDate recommendedDate;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "order_number")
    private Integer orderNumber;
}
