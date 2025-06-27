package org.sxy.optimus.module;

import jakarta.persistence.*;
import org.sxy.optimus.module.compKey.RoomQuizId;

import java.util.Objects;

@Entity
public class RoomQuiz {

    @EmbeddedId
    private RoomQuizId id;

    @ManyToOne
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public RoomQuiz(RoomQuizId id) {
        this.id = id;
    }

    public RoomQuiz() {
    }

    public RoomQuizId getId() {
        return id;
    }

    public void setId(RoomQuizId id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoomQuiz roomQuiz)) return false;
        return Objects.equals(id, roomQuiz.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}