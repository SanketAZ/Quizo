package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;

public class RoomUpdateReqDTO {
    @NotEmpty(message = "Room id cannot be empty")
    private String roomId;

    @NotEmpty(message = "User id cannot be empty")
    private String ownerUserId;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    public RoomUpdateReqDTO() {
    }

    public RoomUpdateReqDTO(String roomId, String description, String title, String ownerUserId) {
        this.roomId = roomId;
        this.description = description;
        this.title = title;
        this.ownerUserId = ownerUserId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
