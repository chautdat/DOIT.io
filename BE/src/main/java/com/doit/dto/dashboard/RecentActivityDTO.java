package com.doit.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {

    private Long id;
    private String activityType; // LISTENING, READING, WRITING, SPEAKING, MOCK_TEST
    private String title;
    private String description;
    private BigDecimal bandScore;
    private String status;
    private LocalDateTime timestamp;
    private Integer timeSpentMinutes;
}
