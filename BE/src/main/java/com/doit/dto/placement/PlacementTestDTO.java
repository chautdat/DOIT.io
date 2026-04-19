package com.doit.dto.placement;

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
public class PlacementTestDTO {

    private Long id;
    private String title;
    private String description;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private List<PlacementSectionDTO> sections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlacementSectionDTO {
        private Exam.Skill skill;
        private Integer questionCount;
        private Integer durationMinutes;
    }
}
