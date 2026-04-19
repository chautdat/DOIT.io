package com.doit.dto.admin;

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
public class SubmissionAdminDTO {

    private Long id;
    private String type; // WRITING or SPEAKING
    private Long userId;
    private String userEmail;
    private String userFullName;
    private Long taskId;
    private String taskTitle;
    private String submissionContent;
    private String audioUrl;
    private Boolean isGraded;
    private BigDecimal bandScore;
    private String feedback;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
}
