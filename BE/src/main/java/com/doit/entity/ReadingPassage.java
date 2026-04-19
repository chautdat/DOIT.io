package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reading_passages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingPassage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    private String title;

    @Column(name = "passage_text", nullable = false, columnDefinition = "TEXT")
    private String passageText;

    @Column(name = "passage_number", nullable = false)
    private Integer passageNumber;

    @Column(name = "word_count")
    private Integer wordCount;

    @OneToMany(mappedBy = "passage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReadingQuestion> questions = new ArrayList<>();
}
