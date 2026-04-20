package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_skill_progress")
@CompoundIndex(name = "user_skill_idx", def = "{'userId': 1, 'skill': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillProgress {

    @Id
    private String id;

    private String userId;

    private String skill;

    @Builder.Default
    private Double currentScore = 0.0;

    @Builder.Default
    private Integer totalAttempts = 0;

    @Builder.Default
    private Integer correctAnswers = 0;

    @Builder.Default
    private Integer totalQuestions = 0;

    @LastModifiedDate
    private LocalDateTime lastUpdated;
}
