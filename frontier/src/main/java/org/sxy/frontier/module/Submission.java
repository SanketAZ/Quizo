package org.sxy.frontier.module;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "submission_id",updatable = false,nullable = false)
    private UUID submissionId;

    @Column(name = "user_id",nullable = false)
    private UUID userId;

    @Column(name = "room_id",nullable = false)
    private UUID roomId;

    @Column(name = "quiz_id",nullable = false)
    private UUID quizId;

    @Column(name = "question_id",nullable = false)
    private UUID questionId;

    @Column(name = "selected_option_id",nullable = false)
    private UUID selectedOptionId;

    @Column(name = "is_correct",nullable = false)
    private Boolean isCorrect;

    @Column(name = "obtained_marks",nullable = false)
    private Integer obtainedMarks;

    @Column(name = "submitted_at",nullable = false)
    private Instant submittedAt;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false,nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false)
    private Instant updatedAt;
}
