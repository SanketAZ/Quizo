package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;

public class RoomUpdateResDTO {

    private String roomId;

    private String ownerUserId;

    private String title;

    private String description;

    public RoomUpdateResDTO() {
    }

    public RoomUpdateResDTO(String roomId, String description, String title, String ownerUserId) {
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
