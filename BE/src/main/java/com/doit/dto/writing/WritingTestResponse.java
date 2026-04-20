package com.doit.dto.writing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingTestResponse {
    private String examId;
    private String title;
    private String description;
    private Integer durationMinutes;
    private List<TaskResponse> tasks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse {
        private String taskId;
        private Integer taskNumber;
        private String taskType; // TASK_1_ACADEMIC, TASK_1_GENERAL, TASK_2
        private String instruction;
        private String question;
        private String imageUrl; // For Task 1 charts/graphs
        private Integer minWords;
        private Integer recommendedTime;
        private List<String> tips;
    }
}
