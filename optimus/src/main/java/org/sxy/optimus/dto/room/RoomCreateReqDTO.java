package org.sxy.optimus.dto.room;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public class RoomCreateReqDTO {
    @NotEmpty(message = "User id cannot be empty")
    private String ownerUserId;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    public RoomCreateReqDTO() {
    }

    public RoomCreateReqDTO(String ownerUserId, String description, String title) {
        this.ownerUserId = ownerUserId;
        this.description = description;
        this.title = title;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
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
}
