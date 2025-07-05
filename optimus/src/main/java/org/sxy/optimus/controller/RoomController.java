package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.dto.PageResponse;
import org.sxy.optimus.dto.quiz.QuizDisplayDTO;
import org.sxy.optimus.dto.room.*;
import org.sxy.optimus.exception.MismatchException;
import org.sxy.optimus.exception.UserIdMismatchException;
import org.sxy.optimus.service.QuizService;
import org.sxy.optimus.service.RoomService;
import org.sxy.optimus.utility.UserContextHolder;

import java.util.UUID;

@RestController
@RequestMapping("/api/room")
@Tag(name = "Room",description = "Room service api's")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private QuizService quizService;

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

    //Get all quizzes present in the room just passing some quiz details
    @PostMapping("/{roomId}/quizzes")
    public ResponseEntity<PageResponse<QuizDisplayDTO>> fetchRoomQuizzesForOwner(@PathVariable String roomId,@RequestParam(value = "status",required = true)String status, @RequestBody PageRequestDTO pageRequestDTO) {
        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        PageResponse<QuizDisplayDTO> requiredQuizzes=quizService.fetchOwnerQuizzesForRoom(userId,status,UUID.fromString(roomId), pageRequestDTO);
        return ResponseEntity
                .ok()
                .body(requiredQuizzes);
    }

    //All Rooms For given owner
    @PostMapping ("/owner")
    public ResponseEntity<PageResponse<RoomDisplayDTO>> fetchRoomsForOwner(@RequestBody PageRequestDTO pageRequestDTO){
        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        PageResponse<RoomDisplayDTO> requiredRooms=roomService.fetchRoomsForOwner(userId,pageRequestDTO);
        return ResponseEntity
                .ok()
                .body(requiredRooms);
    }

    //Adding users to the given room
    @PostMapping("/{roomId}/users")
    public ResponseEntity<String> addUsersToRoom(@PathVariable("roomId")String roomId, @RequestBody AddUsersToRoomRequestDTO requestDTO){
        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        String response=roomService.addUsersToRoom(userId,UUID.fromString(roomId),requestDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

}
