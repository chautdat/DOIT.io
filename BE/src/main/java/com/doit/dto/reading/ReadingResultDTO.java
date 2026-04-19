package com.doit.dto.reading;

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
public class ReadingResultDTO {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private BigDecimal bandScore;
    private Integer timeSpentSeconds;
    private LocalDateTime submittedAt;
    private List<ReadingAnswerResultDTO> answerResults;
}
