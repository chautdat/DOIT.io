package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "speaking_parts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingPart {

    @Id
    private String id;

    private String examId;

    private PartType partType;

    private String topic;

    private String description;

    @Builder.Default
    private List<String> questions = new ArrayList<>();

    private String cueCard;

    private Integer preparationTimeSeconds;

    private Integer speakingTimeSeconds;

    public enum PartType {
        PART_1,
        PART_2,
        PART_3
    }
}
