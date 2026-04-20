package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "writing_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingTask {

    @Id
    private String id;

    private String examId;

    private TaskType taskType;

    private String title;

    private String taskDescription;

    private String imageUrl;

    private String chartData;

    private Integer minWords;

    private Integer maxWords;

    private Integer timeLimitMinutes;

    private String sampleAnswer;

    public enum TaskType {
        TASK_1_ACADEMIC,
        TASK_1_GENERAL,
        TASK_2
    }
}
