package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sxy.optimus.dto.room.*;
import org.sxy.optimus.module.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    Room toRoom(RoomCreateReqDTO roomCreateReqDTO);

    RoomCreateResDTO toRoomCreateResDTO(Room room);

    RoomUpdateResDTO toRoomUpdateResDTO(Room room);

    @Mapping(target = "quizIds",ignore = true)
    RoomDTO toRoomDTO(Room room);

    @Mapping(target = "ownerUserId",ignore = true)
    RoomDisplayDTO toRoomDisplayDTOForOwner(Room room);

}
