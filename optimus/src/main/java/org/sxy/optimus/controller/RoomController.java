package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.room.*;
import org.sxy.optimus.exception.MismatchException;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.RoomService;
import org.sxy.optimus.utility.UserContextHolder;

@RestController
@RequestMapping("/api/room")
@Tag(name = "Room",description = "Room service api's")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    @Operation(summary = "To add Room with some basic details")
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
    @Operation(summary = "To Update single room basic details")
    public ResponseEntity<RoomUpdateResDTO> updateRoom(@RequestBody RoomUpdateReqDTO roomUpdateReqDTO){
        //checking userId in DTO and Principle User
        if(!roomUpdateReqDTO.getOwnerUserId().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(roomUpdateReqDTO.getOwnerUserId(), UserContextHolder.getUser().getId());
        }

        //update the room details
        RoomUpdateResDTO updateResDTO=roomService.updateRoom(roomUpdateReqDTO);

        return ResponseEntity.ok().body(updateResDTO);

    }

    @PostMapping("/{roomID}")
    public ResponseEntity<RoomDTO> addQuizToRoom(@PathVariable("roomID")String roomId, @RequestBody AssignQuizToRoomReqDTO assignQuizToRoomReqDTO){
        //checking userId in DTO and Principle User
        if(!assignQuizToRoomReqDTO.getOwnerUserId().equals(UserContextHolder.getUser().getId())){
            throw new UserIdMismatchException(assignQuizToRoomReqDTO.getOwnerUserId(), UserContextHolder.getUser().getId());
        }

        if(!roomId.equals(assignQuizToRoomReqDTO.getRoomId())){
            throw new MismatchException(assignQuizToRoomReqDTO.getRoomId(), roomId);
        }

        RoomDTO updatedRoom=roomService.addQuizzesToRoom(assignQuizToRoomReqDTO);

        return ResponseEntity
                .ok()
                .body(updatedRoom);

    }

}
