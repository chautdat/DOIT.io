package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "listening_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningAnswer {

    @Id
    private String id;

    private String attemptId;

    private String questionId;

    private String userAnswer;

    @Builder.Default
    private Boolean isCorrect = false;
}
