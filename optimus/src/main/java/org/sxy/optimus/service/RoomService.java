package org.sxy.optimus.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.sxy.optimus.dto.PageRequestDTO;
import org.sxy.optimus.dto.PageResponse;
import org.sxy.optimus.dto.pojo.RoomUserDetails;
import org.sxy.optimus.dto.room.*;
import org.sxy.optimus.event.RoomUserDetailsCachedEvent;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.exception.ValidationException;
import org.sxy.optimus.mapper.QuizMapper;
import org.sxy.optimus.mapper.RoomMapper;
import org.sxy.optimus.module.Room;
import org.sxy.optimus.module.RoomQuiz;
import org.sxy.optimus.module.RoomUser;
import org.sxy.optimus.module.compKey.RoomQuizId;
import org.sxy.optimus.module.compKey.RoomUserId;
import org.sxy.optimus.redis.repo.RoomCacheRepository;
import org.sxy.optimus.repo.QuizRepo;
import org.sxy.optimus.repo.RoomRepo;
import org.sxy.optimus.repo.RoomUserRepo;
import org.sxy.optimus.utility.PageRequestHelper;
import org.sxy.optimus.utility.PageRequestValidator;
import org.sxy.optimus.validation.ValidationResult;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {

    final static Logger log = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private RoomUserRepo roomUserRepo;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private RoomCacheRepository redisRoomRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ApplicationEventPublisher publisher;

    private final RoomMapper roomMapper;

    private final QuizMapper quizMapper;

    @Autowired
    private QuizRepo quizRepo;


    public RoomService(RoomMapper roomMapper, QuizMapper quizMapper) {
        this.roomMapper = roomMapper;
        this.quizMapper = quizMapper;
    }


    //create single room
    public RoomCreateResDTO createRoom(RoomCreateReqDTO roomCreateReqDTO){

        Room roomToCreate=roomMapper.toRoom(roomCreateReqDTO);
        roomRepo.save(roomToCreate);

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

    //Method to add users in the particular room
    @Transactional
    public String addUsersToRoom(UUID userId,UUID roomId,AddUsersToRoomRequestDTO addUsersToRoomRequestDTO){
        //Fetch the Room
        Room room=roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceDoesNotExitsException("Room","roomId",roomId.toString()));


        //validating user has access to the quiz
        if(!userId.toString().equals(room.getOwnerUserId().toString())){
            throw new UnauthorizedActionException("User with id "+ userId +"is not authorized to update the room");
        }

        // build list of all desired composite‐IDs
        List<RoomUserId> allIds = addUsersToRoomRequestDTO.getUserIds().stream()
                .map(s -> new RoomUserId(roomId, UUID.fromString(s)))
                .toList();

        // single query to fetch whaťs already there
        Set<RoomUserId> existing = new HashSet<>(
                roomUserRepo.findExistingRoomUsers(allIds)
        );

        // filter out already-present
        List<RoomUser> toInsert = allIds.stream()
                .filter(id -> !existing.contains(id))
                .map(id -> {
                    RoomUser ru = new RoomUser();
                    ru.setRoomUserId(id);
                    ru.setRoom(room);
                    return ru;
                })
                .toList();

        //*
        for (RoomUser ru : toInsert) {
            em.persist(ru);}
        em.flush();


        log.info("Users added to Room {} By User {}", roomId, userId);

        return "Users are added to the room";
    }

    //Remove users from the room
    public String removeRoomUsers(UUID userId,UUID roomId,RemoveRoomUsersReqDTO reqDTO){
        //Fetch the Room
        Room room=roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceDoesNotExitsException("Room","roomId",roomId.toString()));

        //validating user has access to the quiz
        if(!userId.toString().equals(room.getOwnerUserId().toString()))
            throw new UnauthorizedActionException("User with id "+ userId +"is not authorized to update the room");

        List<RoomUserId>idsToRemove=reqDTO.getUserIds().stream().map(s -> new RoomUserId(roomId,UUID.fromString(s))).toList();

        roomUserRepo.deleteRoomUsersByIds(idsToRemove);

        log.info("User {} deleted users from room {}",userId, roomId);

        return "Users Removed form the Room";

    }

    public PageResponse<RoomDisplayDTO> fetchOwnedRooms(UUID userId, PageRequestDTO pageRequestDTO){
        // Validate sort field
        List<ValidationResult> errors= PageRequestValidator.validatePageRequest(pageRequestDTO, List.of("createdAt","updatedAt","title"));
        if(!errors.isEmpty()){
            throw new ValidationException("Validation failed PageRequestDTO",errors);
        }

        Pageable pageable= PageRequestHelper.toPageable(pageRequestDTO);

        Page<Room> roomPage = roomRepo.findByOwnerUserId(userId, pageable);
        List<RoomDisplayDTO> content = roomPage.getContent().stream()
                .map(room -> {
                    RoomDisplayDTO dto = roomMapper.toRoomDisplayDTOForOwner(room);
                    dto.setOwnerUserId(room.getOwnerUserId().toString());
                    return dto;
                })
                .toList();

        log.info("User {} fetched {} owned rooms (page {})", userId, content.size(), pageRequestDTO.getPageNo());

        return PageResponse.of(content, roomPage);
    }

    public PageResponse<RoomDisplayDTO> fetchJoinedRooms(UUID userId, PageRequestDTO pageRequestDTO){
        // Validate sort field
        List<ValidationResult> errors= PageRequestValidator.validatePageRequest(pageRequestDTO, List.of("createdAt","updatedAt"));
        if(!errors.isEmpty()){
            throw new ValidationException("Validation failed PageRequestDTO",errors);
        }

        Pageable pageable= PageRequestHelper.toPageable(pageRequestDTO);

        Page<Room> roomPage = roomRepo.findRoomsWhereUserIsParticipant(userId, pageable);
        List<RoomDisplayDTO> content = roomPage.getContent().stream()
                .map(room -> {
                    RoomDisplayDTO dto = roomMapper.toRoomDisplayDTOForOwner(room);
                    dto.setOwnerUserId(room.getOwnerUserId().toString());
                    return dto;
                })
                .toList();

        log.info("User {} fetched {} joined rooms (page {})", userId, content.size(), pageRequestDTO.getPageNo());

        return PageResponse.of(content, roomPage);
    }

    //This method is to upload all the users in the room to redis with ttl provided
    public void cacheRoomUsersDetailToRedis(UUID roomId, Duration ttl){
        //check room exists or not
        if(!roomRepo.existsById(roomId)){
            throw new ResourceDoesNotExitsException("Room","roomId",roomId.toString());
        }

        //fetch the users in room
        List<UUID> userIds=roomUserRepo.findUserIdsInRoom(roomId);

        if (userIds.isEmpty()){
            log.info("No users found for room {}", roomId);
            return;
        }

        //get the user details using there id
        //This implementation will be modified later
        List<RoomUserDetails>userDetailsToUpload=userIds.stream()
                .map(uuid -> new RoomUserDetails(uuid.toString(),"username1"))
                .toList();

        try {
            //upload the room users details to redis
            redisRoomRepository.cacheRoomUserDetails(userDetailsToUpload,roomId,ttl.toSeconds());
        }catch (DataAccessException e){
            log.error("Exception occurred while uploading the room users details to redis {}",e.getMessage());
        }

        log.info("Cached {} users for room {} in Redis", userDetailsToUpload.size(), roomId);
    }

    public RoomUserDetails getRoomUsersDetailCache(UUID roomId, UUID userId){
        Optional<RoomUserDetails> ops=redisRoomRepository.getRoomUserDetailsCache(roomId,userId);
        if(ops.isPresent()){
            return ops.get();
        }
        //check room exists or not
        if(!roomRepo.existsById(roomId)) {
            throw new ResourceDoesNotExitsException("Room","roomId",roomId.toString());
        }
        if(!roomUserRepo.existsById(new RoomUserId(roomId,userId))) {
            log.warn("User {} is not present in room {}", userId, roomId);
            String msg=String.format("User with id: %s does not present in Room with id: %s",userId,roomId);
            throw new UnauthorizedActionException(msg);
        }

        //Here we will call the user service to get more info about the user
        //for now creating the fake object
        RoomUserDetails roomUserDetails=new RoomUserDetails(userId.toString(),"username1");

        publisher.publishEvent(new RoomUserDetailsCachedEvent(roomId,roomUserDetails,Duration.ofSeconds(900)));
        return roomUserDetails;
    }

}
