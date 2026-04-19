package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listening_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_id", nullable = false)
    private ListeningAudio audio;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @ElementCollection
    @CollectionTable(name = "listening_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    @Builder.Default
    private List<String> options = new ArrayList<>();

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    public enum QuestionType {
        FILL_IN_THE_BLANK,
        MULTIPLE_CHOICE,
        MATCHING,
        MAP_LABELING,
        NOTE_COMPLETION,
        SENTENCE_COMPLETION
    }
}
