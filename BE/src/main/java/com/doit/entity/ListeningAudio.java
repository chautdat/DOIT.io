package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "listening_audios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningAudio {

    @Id
    private String id;

    private String examId;

    private String audioUrl;

    private String transcript;

    private Integer partNumber;

    private String partTitle;

    private Integer durationSeconds;

    @Builder.Default
    private List<ListeningQuestion> questions = new ArrayList<>();
}
