package org.sxy.optimus.dto.room;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RemoveRoomUsersReqDTO {
    @NotEmpty
    private String roomId;

    @NotEmpty
    private List<@NotNull String> userIds;

    public RemoveRoomUsersReqDTO() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
