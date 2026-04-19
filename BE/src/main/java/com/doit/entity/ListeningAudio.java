package com.doit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listening_audios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningAudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "part_number", nullable = false)
    private Integer partNumber;

    @Column(name = "part_title")
    private String partTitle;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @OneToMany(mappedBy = "audio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ListeningQuestion> questions = new ArrayList<>();
}
