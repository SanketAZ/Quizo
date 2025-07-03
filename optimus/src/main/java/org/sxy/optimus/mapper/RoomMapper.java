package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.room.*;
import org.sxy.optimus.module.Room;
import org.sxy.optimus.module.RoomQuiz;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
