package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "speaking_parts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "part_number", nullable = false)
    private Integer partNumber; // 1, 2, or 3

    @Column(nullable = false)
    private String topic;

    @Column(name = "topic_description", columnDefinition = "TEXT")
    private String topicDescription;

    @ElementCollection
    @CollectionTable(name = "speaking_questions", joinColumns = @JoinColumn(name = "part_id"))
    @Column(name = "question", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> questions = new ArrayList<>();

    @Column(name = "cue_card", columnDefinition = "TEXT")
    private String cueCard; // For Part 2

    @Column(name = "prep_time_seconds")
    @Builder.Default
    private Integer prepTimeSeconds = 60; // Part 2: 1 minute prep

    @Column(name = "speak_time_seconds")
    private Integer speakTimeSeconds; // Part 1: 4-5 min, Part 2: 2 min, Part 3: 4-5 min

    @Column(name = "sample_answers", columnDefinition = "TEXT")
    private String sampleAnswers;

    @Column(columnDefinition = "TEXT")
    private String tips;
}
