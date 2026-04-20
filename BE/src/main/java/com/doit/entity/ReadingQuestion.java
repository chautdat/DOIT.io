package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "reading_questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingQuestion {

    @Id
    private String id;

    private String passageId;

    private String questionText;

    private QuestionType questionType;

    private String correctAnswer;

    @Builder.Default
    private List<String> options = new ArrayList<>();

    private Integer orderNumber;

    private String explanation;

    public enum QuestionType {
        TRUE_FALSE_NOT_GIVEN,
        YES_NO_NOT_GIVEN,
        MATCHING_HEADINGS,
        MATCHING_INFORMATION,
        MATCHING_FEATURES,
        MULTIPLE_CHOICE,
        SENTENCE_COMPLETION,
        SUMMARY_COMPLETION,
        NOTE_COMPLETION,
        TABLE_COMPLETION,
        FLOW_CHART_COMPLETION,
        DIAGRAM_LABELING,
        SHORT_ANSWER
    }
}
