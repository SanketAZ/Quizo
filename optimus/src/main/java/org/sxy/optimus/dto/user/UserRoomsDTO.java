package org.sxy.optimus.dto.user;

import org.sxy.optimus.dto.room.RoomDisplayDTO;

import java.util.List;

public class UserRoomsDTO {
    private String ownerUserId;
    List<RoomDisplayDTO> rooms;

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public List<RoomDisplayDTO> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomDisplayDTO> rooms) {
        this.rooms = rooms;
    }
}
