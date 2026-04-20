package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "listening_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningQuestion {

    @Id
    private String id;

    private String audioId;

    private String questionText;

    private QuestionType questionType;

    private String correctAnswer;

    @Builder.Default
    private List<String> options = new ArrayList<>();

    private Integer orderNumber;

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
