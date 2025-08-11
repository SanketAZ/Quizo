package org.sxy.optimus.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_Id",nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;

    @Column(name = "questionId", insertable = false, updatable = false)
    private UUID questionId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UUID getQuestionId() {
        return questionId;
    }
}
