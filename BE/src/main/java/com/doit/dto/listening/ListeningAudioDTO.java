package com.doit.dto.listening;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningAudioDTO {
    private Long id;
    private Integer partNumber;
    private String title;
    private String audioUrl;
    private Integer durationSeconds;
    private List<ListeningQuestionDTO> questions;
}
