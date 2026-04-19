package com.doit.dto.listening;

import com.doit.entity.ListeningQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningQuestionDTO {
    private Long id;
    private Integer orderNumber;
    private ListeningQuestion.QuestionType questionType;
    private String questionText;
    private String options; // JSON array for multiple choice
    private Integer points;
}
