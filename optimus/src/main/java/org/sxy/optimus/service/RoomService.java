package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.sxy.optimus.dto.room.RoomCreateReqDTO;
import org.sxy.optimus.dto.room.RoomCreateResDTO;
import org.sxy.optimus.dto.room.RoomUpdateReqDTO;
import org.sxy.optimus.dto.room.RoomUpdateResDTO;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.mapper.RoomMapper;
import org.sxy.optimus.module.Room;
import org.sxy.optimus.repo.RoomRepo;

import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    private RoomRepo roomRepo;

    private final RoomMapper roomMapper;

    final static Logger log = LoggerFactory.getLogger(RoomService.class);


    public RoomService(RoomMapper roomMapper) {
        this.roomMapper = roomMapper;
    }


    //create single room
    public RoomCreateResDTO createRoom(RoomCreateReqDTO roomCreateReqDTO){

        Room roomToCreate=roomMapper.toRoom(roomCreateReqDTO);

        log.info("New room created: {}", roomToCreate);

        return roomMapper.toRoomCreateResDTO(roomRepo.save(roomToCreate));
    }

    //update room details
    public RoomUpdateResDTO updateRoom(RoomUpdateReqDTO roomUpdateReqDTO){

        //Fetch the Room
        Room existRoom=roomRepo.findById(UUID.fromString(roomUpdateReqDTO.getRoomId()))
                .orElseThrow(() -> new ResourceDoesNotExitsException("Room","roomId",roomUpdateReqDTO.getRoomId()));

        //Update the Room details
        existRoom.setDescription(roomUpdateReqDTO.getDescription());
        existRoom.setTitle(roomUpdateReqDTO.getTitle());

        log.info("Room details before update: {}", existRoom);

        Room updateRoom=roomRepo.save(existRoom);

        log.info("Room updated: {}", updateRoom);

        return roomMapper.toRoomUpdateResDTO(updateRoom);

    }
}
