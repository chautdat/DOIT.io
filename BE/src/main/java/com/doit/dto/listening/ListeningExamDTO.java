package com.doit.dto.listening;

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
public class ListeningExamDTO {
    private String examId;
    private String title;
    private String description;
    private Exam.BandLevel bandLevel;
    private Exam.ExamType examType;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private List<ListeningAudioDTO> audioParts;
}
