package com.doit.dto.reading;

import com.doit.entity.ReadingQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingQuestionDTO {
    private String id;
    private Integer orderNumber;
    private ReadingQuestion.QuestionType questionType;
    private String questionText;
    private String options; // Pipe-separated for multiple choice
    private Integer points;
}
