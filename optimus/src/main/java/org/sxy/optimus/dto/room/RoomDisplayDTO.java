package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;
import org.sxy.optimus.dto.quiz.QuizDisplayDTO;

import java.util.List;

public class RoomDisplayDTO {
    private String roomId;

    private String ownerUserId;

    private String title;

    private String description;

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

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
