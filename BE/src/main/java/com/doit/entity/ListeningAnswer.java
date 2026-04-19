package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "listening_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private UserAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private ListeningQuestion question;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;
}
