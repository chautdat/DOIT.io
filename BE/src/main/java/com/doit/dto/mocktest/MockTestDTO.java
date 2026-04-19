package com.doit.dto.mocktest;

import com.doit.entity.MockTest;
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
public class MockTestDTO {

    private Long id;
    private Long userId;
    private MockTest.MockTestStatus status;
    private BigDecimal listeningBand;
    private BigDecimal readingBand;
    private BigDecimal writingBand;
    private BigDecimal speakingBand;
    private BigDecimal overallBand;
    private Long listeningAttemptId;
    private Long readingAttemptId;
    private Long writingAttemptId;
    private Long speakingAttemptId;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer totalTimeMinutes;
}
