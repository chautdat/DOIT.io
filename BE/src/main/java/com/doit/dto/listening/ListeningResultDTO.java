package com.doit.dto.listening;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningResultDTO {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private BigDecimal bandScore;
    private Integer timeSpentSeconds;
    private LocalDateTime submittedAt;
    private List<ListeningAnswerResultDTO> answerResults;
}
