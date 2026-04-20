package com.doit.dto.listening;

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
public class ListeningResultDTO {
    private String attemptId;
    private String examId;
    private String examTitle;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double bandScore;
    private Integer timeSpentSeconds;
    private LocalDateTime submittedAt;
    private List<ListeningAnswerResultDTO> answerResults;
}
