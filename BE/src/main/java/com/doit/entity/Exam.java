package com.doit.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Document(collection = "exams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    private String id;

    private String title;

    private String description;

    private ExamType type;

    private Skill skill;

    private BandLevel bandLevel;

    private Integer durationMinutes;

    private Integer totalQuestions;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isFree = false;

    @DBRef
    private User createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum ExamType {
        PRACTICE,       // Practice individual skill
        PLACEMENT,      // Placement test
        MOCK_TEST,      // Full mock test
        ACADEMIC,       // Academic module
        GENERAL         // General Training module
    }

    public enum Skill {
        LISTENING,
        READING,
        WRITING,
        SPEAKING
    }

    public enum BandLevel {
        BEGINNER,       // 0.0 - 4.5
        ELEMENTARY,     // 5.0 - 6.0
        INTERMEDIATE,   // 6.5 - 7.0
        ADVANCED        // 7.5 - 9.0
    }
}
