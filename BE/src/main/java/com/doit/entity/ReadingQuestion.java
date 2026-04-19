package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reading_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passage_id", nullable = false)
    private ReadingPassage passage;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @ElementCollection
    @CollectionTable(name = "reading_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    @Builder.Default
    private List<String> options = new ArrayList<>();

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    public enum QuestionType {
        TRUE_FALSE_NOT_GIVEN,
        YES_NO_NOT_GIVEN,
        MULTIPLE_CHOICE,
        MATCHING_HEADINGS,
        MATCHING_INFORMATION,
        MATCHING_FEATURES,
        FILL_IN_THE_BLANK,
        SENTENCE_COMPLETION,
        SUMMARY_COMPLETION,
        SHORT_ANSWER
    }
}
