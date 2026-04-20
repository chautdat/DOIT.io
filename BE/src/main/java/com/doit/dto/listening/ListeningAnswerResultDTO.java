package com.doit.dto.listening;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningAnswerResultDTO {
    private String questionId;
    private Integer orderNumber;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private String questionText;
}
