package com.doit.dto.writing;

import com.doit.entity.WritingTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingTaskDTO {
    private String id;
    private Integer taskNumber;
    private WritingTask.TaskType taskType;
    private String promptText;
    private String imageUrl;
    private Integer minWords;
    private String tips;
}
