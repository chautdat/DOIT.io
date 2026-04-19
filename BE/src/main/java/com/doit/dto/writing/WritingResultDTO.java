package com.doit.dto.writing;

import com.doit.entity.WritingTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingResultDTO {
    private Long submissionId;
    private Long attemptId;
    private Long examId;
    private String examTitle;
    
    // Task info
    private Long taskId;
    private Integer taskNumber;
    private WritingTask.TaskType taskType;
    private String promptText;
    
    // Submission info
    private String userEssay;
    private Integer wordCount;
    private Integer timeSpentSeconds;
    private LocalDateTime submittedAt;
    
    // Scores
    private BigDecimal bandScore;
    private BigDecimal taskResponseScore;
    private BigDecimal coherenceCohesionScore;
    private BigDecimal lexicalResourceScore;
    private BigDecimal grammarAccuracyScore;
    
    // AI Feedback
    private String aiFeedback;
    private String aiModel;
    
    // Reference
    private String sampleAnswer;
}
