package com.doit.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionAdminDTO {

    private String id;
    private String type; // WRITING or SPEAKING
    private String userId;
    private String userEmail;
    private String userFullName;
    private String taskId;
    private String taskTitle;
    private String submissionContent;
    private String audioUrl;
    private Boolean isGraded;
    private Double bandScore;
    private String feedback;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
}
