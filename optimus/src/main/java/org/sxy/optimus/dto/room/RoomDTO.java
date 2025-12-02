package org.sxy.optimus.dto.room;


import jakarta.validation.constraints.NotEmpty;


import java.util.List;


public class RoomDTO {
    @NotEmpty(message = "Room id is required")
    private String roomId;

    @NotEmpty(message = "Owner user id is required")
    private String ownerUserId;

    @NotEmpty(message = "Room title is required")
    private String title;

    @NotEmpty(message = "Room description is required")
    private String description;

    @NotEmpty(message = "List cannot be empty")
    private List<String> quizIds;

    public RoomDTO() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getQuizIds() {
        return quizIds;
    }

    public void setQuizIds(List<String> quizIds) {
        this.quizIds = quizIds;
    }
}
