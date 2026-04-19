package com.doit.dto.reading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingPassageDTO {
    private Long id;
    private Integer passageNumber;
    private String title;
    private String passageText;
    private Integer wordCount;
    private List<ReadingQuestionDTO> questions;
}
