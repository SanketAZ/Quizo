package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AssignQuizToRoomReqDTO {
    @NotEmpty(message = "Room id is required")
    private String roomId;

    @NotEmpty(message = "List cannot be empty")
    private List<String> quizIds;

    @NotEmpty(message = "Owner user id is required")
    private String ownerUserId;

    public AssignQuizToRoomReqDTO() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getQuizIds() {
        return quizIds;
    }

    public void setQuizIds(List<String> quizIds) {
        this.quizIds = quizIds;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
