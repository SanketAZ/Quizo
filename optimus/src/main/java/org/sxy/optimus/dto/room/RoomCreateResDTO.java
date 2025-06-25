package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public class RoomCreateResDTO {

    private String roomId;

    private UUID ownerUserId;

    private String title;

    private String description;

    public RoomCreateResDTO() {
    }

    public RoomCreateResDTO(String roomId, String description, String title, UUID ownerUserId) {
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

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
