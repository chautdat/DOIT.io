package com.doit.dto.reading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingResultDTO {
    private String attemptId;
    private String examId;
    private String examTitle;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double bandScore;
    private Integer timeSpentSeconds;
    private LocalDateTime submittedAt;
    private List<ReadingAnswerResultDTO> answerResults;
}
