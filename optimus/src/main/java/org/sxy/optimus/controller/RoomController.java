package org.sxy.optimus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room",description = "Room management APIs")
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

    @GetMapping("/owned")
    public ResponseEntity<PageResponse<RoomDisplayDTO>> getOwnedRooms(
            @RequestParam(defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @RequestParam(defaultValue = "createdAt")String sortBy,
            @RequestParam(defaultValue = "DESC") @Pattern(regexp = "ASC|DESC") String sortOrder
    ){
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());

        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();

        PageResponse<RoomDisplayDTO> rooms = roomService.fetchOwnedRooms(
                userId, pageRequestDTO
        );

        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/joined")
    public ResponseEntity<PageResponse<RoomDisplayDTO>> getJoinedRooms(
            @RequestParam(defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @RequestParam(defaultValue = "createdAt")String sortBy,
            @RequestParam(defaultValue = "DESC") @Pattern(regexp = "ASC|DESC") String sortOrder
    ){
        UUID userId = UUID.fromString(UserContextHolder.getUser().getId());
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();

        PageResponse<RoomDisplayDTO> rooms = roomService.fetchJoinedRooms(
                userId, pageRequestDTO
        );

        return ResponseEntity.ok(rooms);
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

    //Remove users from the given room
    @DeleteMapping("/{roomId}/users")
    public ResponseEntity<String> removeRoomUsers(@PathVariable("roomId")String roomId, @RequestBody RemoveRoomUsersReqDTO requestDTO){
        UUID userId=UUID.fromString(UserContextHolder.getUser().getId());
        String response=roomService.removeRoomUsers(userId,UUID.fromString(roomId),requestDTO);
        return ResponseEntity
                .ok()
                .body(response);
    }

}
