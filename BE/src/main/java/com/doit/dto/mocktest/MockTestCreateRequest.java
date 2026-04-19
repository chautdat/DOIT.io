package com.doit.dto.mocktest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTestCreateRequest {

    private Long listeningExamId;
    private Long readingExamId;
    private Long writingExamId;
    private Long speakingExamId;
}
