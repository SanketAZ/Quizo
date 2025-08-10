package org.sxy.optimus.module;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sxy.optimus.exception.UnauthorizedActionException;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "quiz_question_sequence",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_pos", columnNames = {"quiz_id", "sequence_label", "position"}),
                @UniqueConstraint(name = "uq_question", columnNames = {"quiz_id", "question_id", "sequence_label"})
        }
 )
public class QuizQuestionSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "quiz_Id", nullable = false)
    private UUID quizId;

    @Column(name = "question_Id", nullable = false)
    private UUID questionId;

    @Column(name = "sequence_label", nullable = false)
    private String sequenceLabel;

    @Column(name = "position", nullable = false)
    private Integer position;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false,nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",updatable = false,nullable = false)
    private Instant updatedAt;

    public QuizQuestionSequence() {
    }

    public QuizQuestionSequence(UUID id, UUID quizId, UUID questionId, String sequenceLabel, Integer position) {
        this.id = id;
        this.quizId = quizId;
        this.questionId = questionId;
        this.sequenceLabel = sequenceLabel;
        this.position = position;
    }

    public QuizQuestionSequence(UUID questionId, UUID quizId, String sequenceLabel, Integer position) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.sequenceLabel = sequenceLabel;
        this.position = position;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public String getSequenceLabel() {
        return sequenceLabel;
    }

    public void setSequenceLabel(String sequenceLabel) {
        this.sequenceLabel = sequenceLabel;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
