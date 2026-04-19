package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "writing_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "task_number", nullable = false)
    private Integer taskNumber; // 1 or 2

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @Column(name = "prompt_text", nullable = false, columnDefinition = "TEXT")
    private String promptText;

    @Column(name = "image_url")
    private String imageUrl; // For Task 1 charts/graphs

    @Column(name = "min_words", nullable = false)
    private Integer minWords; // Task 1: 150, Task 2: 250

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;

    @Column(name = "tips", columnDefinition = "TEXT")
    private String tips;

    public enum TaskType {
        // Task 1 types
        LINE_GRAPH,
        BAR_CHART,
        PIE_CHART,
        TABLE,
        PROCESS_DIAGRAM,
        MAP,
        LETTER_FORMAL,
        LETTER_INFORMAL,
        
        // Task 2 types
        OPINION,
        DISCUSSION,
        PROBLEM_SOLUTION,
        ADVANTAGES_DISADVANTAGES,
        TWO_PART_QUESTION
    }
}
