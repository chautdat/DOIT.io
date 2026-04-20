package com.doit.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressChartDTO {

    private String skill;
    private List<ProgressPointDTO> dataPoints;
    private Double averageBand;
    private Double highestBand;
    private Double lowestBand;
}
