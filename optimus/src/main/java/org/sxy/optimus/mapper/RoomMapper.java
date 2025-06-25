package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.room.RoomCreateReqDTO;
import org.sxy.optimus.dto.room.RoomCreateResDTO;
import org.sxy.optimus.dto.room.RoomUpdateResDTO;
import org.sxy.optimus.module.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    Room toRoom(RoomCreateReqDTO roomCreateReqDTO);

    RoomCreateResDTO toRoomCreateResDTO(Room room);

    RoomUpdateResDTO toRoomUpdateResDTO(Room room);
}
