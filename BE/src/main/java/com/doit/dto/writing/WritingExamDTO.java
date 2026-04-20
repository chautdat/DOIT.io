package com.doit.dto.writing;

import com.doit.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingExamDTO {
    private String examId;
    private String title;
    private String description;
    private Exam.BandLevel bandLevel;
    private Exam.ExamType examType;
    private Integer durationMinutes;
    private Integer totalTasks;
    private List<WritingTaskDTO> tasks;
}
