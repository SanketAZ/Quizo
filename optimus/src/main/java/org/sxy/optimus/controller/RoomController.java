package org.sxy.optimus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.room.RoomCreateReqDTO;
import org.sxy.optimus.dto.room.RoomCreateResDTO;
import org.sxy.optimus.dto.room.RoomUpdateReqDTO;
import org.sxy.optimus.dto.room.RoomUpdateResDTO;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.RoomService;
import org.sxy.optimus.utility.UserContextHolder;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomCreateResDTO> createRoom(@RequestBody RoomCreateReqDTO roomCreateReqDTO){
        //checking userId in DTO and Principle User
        if(!roomCreateReqDTO.getOwnerUserId().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(roomCreateReqDTO.getOwnerUserId(), UserContextHolder.getUser().getId());
        }

        //Create room
        RoomCreateResDTO createdRoom=roomService.createRoom(roomCreateReqDTO);

        return ResponseEntity.ok().body(createdRoom);
    }

    @PutMapping
    public ResponseEntity<RoomUpdateResDTO> updateRoom(@RequestBody RoomUpdateReqDTO roomUpdateReqDTO){
        //checking userId in DTO and Principle User
        if(!roomUpdateReqDTO.getOwnerUserId().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(roomUpdateReqDTO.getOwnerUserId(), UserContextHolder.getUser().getId());
        }

        //update the room details
        RoomUpdateResDTO updateResDTO=roomService.updateRoom(roomUpdateReqDTO);

        return ResponseEntity.ok().body(updateResDTO);

    }

}
