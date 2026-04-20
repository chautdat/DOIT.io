package com.doit.dto.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingPartDTO {

    private String id;
    private Integer partNumber;
    private String topic;
    private String topicDescription;
    private List<String> questions;
    private String cueCard;
    private Integer prepTimeSeconds;
    private Integer speakTimeSeconds;
    private String tips;
}
