package com.doit.dto.speaking;

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
public class SpeakingExamDTO {

    private String examId;
    private String title;
    private String description;
    private Exam.ExamType examType;
    private Exam.BandLevel bandLevel;
    private Integer durationMinutes;
    private Integer totalParts;
    private List<SpeakingPartDTO> parts;
}
