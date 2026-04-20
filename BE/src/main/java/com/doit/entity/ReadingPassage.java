package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "reading_passages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingPassage {

    @Id
    private String id;

    private String examId;

    private String title;

    private String passageText;

    private Integer passageNumber;

    private Integer wordCount;

    @Builder.Default
    private List<ReadingQuestion> questions = new ArrayList<>();
}
