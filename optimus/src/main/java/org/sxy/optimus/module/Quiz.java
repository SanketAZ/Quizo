package org.sxy.optimus.module;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.sxy.optimus.enums.QuizStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "quiz")
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

    @Column(name = "status")
    private String status = QuizStatus.NOT_STARTED.toString();

    @CreationTimestamp
    @Column(name = "created_at",updatable = false,nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",updatable = false,nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "quiz",fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions;

    @OneToMany(mappedBy = "quiz",fetch = FetchType.LAZY)
    private Set<RoomQuiz> roomQuizes;

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }

    public Set<RoomQuiz> getRoomQuizes() {
        return roomQuizes;
    }

    public void setRoomQuizes(Set<RoomQuiz> roomQuizes) {
        this.roomQuizes = roomQuizes;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(Integer durationSec) {
        this.durationSec = durationSec;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(UUID creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Quiz quiz)) return false;
        return Objects.equals(quizId, quiz.quizId) && Objects.equals(creatorUserId, quiz.creatorUserId) && Objects.equals(title, quiz.title) && Objects.equals(description, quiz.description) && Objects.equals(questionCount, quiz.questionCount) && Objects.equals(durationSec, quiz.durationSec) && Objects.equals(startTime, quiz.startTime) && Objects.equals(createdAt, quiz.createdAt) && Objects.equals(updatedAt, quiz.updatedAt) && Objects.equals(questions, quiz.questions) && Objects.equals(roomQuizes, quiz.roomQuizes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, creatorUserId, title, description, questionCount, durationSec, startTime, createdAt, updatedAt, questions, roomQuizes);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "quizId=" + quizId +
                ", creatorUserId=" + creatorUserId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", questionCount=" + questionCount +
                ", durationSec=" + durationSec +
                ", startTime=" + startTime +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}