package org.sxy.optimus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.room.*;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.exception.ValidationException;
import org.sxy.optimus.mapper.RoomMapper;
import org.sxy.optimus.module.Room;
import org.sxy.optimus.module.RoomQuiz;
import org.sxy.optimus.module.compKey.RoomQuizId;
import org.sxy.optimus.repo.QuizRepo;
import org.sxy.optimus.repo.RoomRepo;
import org.sxy.optimus.validation.ValidationResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private ValidationService validationService;

    private final RoomMapper roomMapper;

    final static Logger log = LoggerFactory.getLogger(RoomService.class);
    @Autowired
    private QuizRepo quizRepo;


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

    //Assign quiz's to room
    public RoomDTO addQuizzesToRoom(AssignQuizToRoomReqDTO assignQuizToRoomReqDTO){

        //validating provided quiz ids
        List<ValidationResult> validationResults=validationService.validateQuizIds(assignQuizToRoomReqDTO.getQuizIds());
        if(!validationResults.isEmpty()){
            throw new ValidationException("Validation failed for one or more quiz ids",validationResults);
        }

        //Fetch the Room
        UUID roomId=UUID.fromString(assignQuizToRoomReqDTO.getRoomId());
        Room room=roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceDoesNotExitsException("Room","roomId",assignQuizToRoomReqDTO.getRoomId()));


        //validating user has access to the quiz
        if(!assignQuizToRoomReqDTO.getOwnerUserId().equals(room.getOwnerUserId().toString())){
            throw new UnauthorizedActionException("User with id "+assignQuizToRoomReqDTO.getOwnerUserId() +"is not authorized to update the room");
        }

        //Adding QuizRoom to Room
        Set<RoomQuiz> roomQuizToAdd=assignQuizToRoomReqDTO.getQuizIds().stream()
                .map(s -> {
                    RoomQuiz roomQuiz=new RoomQuiz(new RoomQuizId(roomId,UUID.fromString(s)));
                    roomQuiz.setRoom(room);
                    roomQuiz.setQuiz(quizRepo.getReferenceById(UUID.fromString(s)));
                    return roomQuiz;
                })
                .collect(Collectors.toSet());

        Set<RoomQuiz> existingRoomQuiz=new HashSet<>(room.getRoomQuizes());
        existingRoomQuiz.addAll(roomQuizToAdd);
        room.setRoomQuizes(existingRoomQuiz);
        Room updatedRoom=roomRepo.save(room);

        //List of quiz ids for mapper to obtain RoomDTO
        List<String> allQuizIds=existingRoomQuiz.stream()
                .map(roomQuiz -> roomQuiz.getId().getQuizId().toString())
                .toList();

        log.info("Quizzes added to room: {} are: {}", roomId, allQuizIds);

        //Adding list of quiz ids to dto
        RoomDTO resDTO=roomMapper.toRoomDTO(updatedRoom);
        resDTO.setQuizIds(allQuizIds);

        return resDTO;

    }
}
