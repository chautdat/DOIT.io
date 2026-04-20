package com.doit.dto.writing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingResultDTO {
    private String submissionId;
    private String attemptId;
    private String examId;
    private String taskId;
    private String content;
    private Integer wordCount;
    private LocalDateTime submittedAt;
    private Double bandScore;
}
