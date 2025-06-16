package org.sxy.optimus.module.compKey;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RoomQuizId implements Serializable {

    private UUID roomId;
    private UUID quizId;

    public RoomQuizId() {

    }

    public RoomQuizId(UUID roomId, UUID quizId) {
        this.roomId = roomId;
        this.quizId = quizId;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public void setQuizId(UUID quizId) {
        this.quizId = quizId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoomQuizId that)) return false;
        return Objects.equals(roomId, that.roomId) && Objects.equals(quizId, that.quizId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, quizId);
    }
}