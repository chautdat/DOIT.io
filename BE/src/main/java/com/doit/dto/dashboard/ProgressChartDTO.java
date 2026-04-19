package com.doit.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressChartDTO {

    private String skill;
    private List<ProgressPointDTO> dataPoints;
    private BigDecimal averageBand;
    private BigDecimal highestBand;
    private BigDecimal lowestBand;
}
