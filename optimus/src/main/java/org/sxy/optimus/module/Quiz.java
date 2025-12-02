package org.sxy.optimus.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sxy.optimus.enums.QuizStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "quiz")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "quiz_id",updatable = false,nullable = false)
    private UUID quizId;

    @Column(name = "creator_user_id",nullable = false)
    private UUID creatorUserId;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "question_count")
    private Integer questionCount;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "start_time",nullable = true)
    private Instant startTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuizStatus status = QuizStatus.NOT_STARTED;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false,nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",updatable = false,nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "quiz",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
}