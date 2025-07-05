package org.sxy.optimus.module;

import jakarta.persistence.*;
import org.sxy.optimus.module.compKey.RoomUserId;

import java.util.UUID;

@Entity
public class RoomUser {

    @EmbeddedId
    @AttributeOverride(name = "useId",column = @Column(name = "user_id"))//*
    private RoomUserId roomUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private Room room;

    public RoomUserId getRoomUserId() {
        return roomUserId;
    }

    public void setRoomUserId(RoomUserId roomUserId) {
        this.roomUserId = roomUserId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
