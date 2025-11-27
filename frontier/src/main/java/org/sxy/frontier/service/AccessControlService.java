package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.dto.QuizDetailDTO;
import org.sxy.frontier.exception.InvalidSubmissionException;
import org.sxy.frontier.exception.QuizNotActiveException;
import org.sxy.frontier.exception.UnauthorizedActionException;
import org.sxy.frontier.redis.repo.RoomCacheRepo;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AccessControlService {

    private static final Logger log = LoggerFactory.getLogger(AccessControlService.class);
    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizDataService quizDataService;
    @Autowired
    private SubmissionDataService submissionDataService;
    @Autowired
    private RoomCacheRepo roomCacheRepo;
    @Autowired
    private OptimusServiceClient  optimusServiceClient;
    @Autowired
    private ParticipantQuizSessionService participantQuizSessionService;

    public void validateUserInRoom(UUID roomId,UUID userId){
        if(!roomCacheRepo.isRoomUserPresent(roomId,userId)){
            optimusServiceClient.getRoomUserDetails(roomId,userId);
        }

    }

    public void validateQuizLive(UUID quizId,UUID roomId){
        QuizDetailDTO quizDetail= quizDataService.getQuizDetail(roomId,quizId);
        Instant currentTime=Instant.now();
        Instant startTime=quizDetail.getStartTime();
        Duration duration=Duration.ofSeconds(quizDetail.getDurationSec());
        Instant endTime=startTime.plus(duration);

        if(currentTime.isBefore(startTime)){
            throw new QuizNotActiveException(QuizNotActiveException.Reason.NOT_STARTED,roomId,quizId);
        }
        if(currentTime.isAfter(endTime)){
            throw new QuizNotActiveException(QuizNotActiveException.Reason.FINISHED,roomId,quizId);
        }
    }

    public void validateParticipantQuizSession(UUID sessionId,UUID userId){
        log.debug("Validating participant quiz session. sessionId={}, userId={}", sessionId, userId);

        ParticipantQuizSessionDTO participantQuizSessionDTO=participantQuizSessionService.getParticipantQuizSession(sessionId);

        if(!userId.equals(participantQuizSessionDTO.getUserId())){
            String msg=String.format("User %s is not allowed to access participant quiz session %s.",userId, sessionId);
            throw new UnauthorizedActionException(msg);
        }

        Instant currentTime=Instant.now();
        Instant finalEndTime=participantQuizSessionDTO.getFinalEndTime();
        if(!currentTime.isBefore(finalEndTime)){
            log.info("Participant quiz session {} is not active. now={}, finalEndTime={}", sessionId, currentTime, finalEndTime);
            throw new QuizNotActiveException(QuizNotActiveException.Reason.FINISHED,participantQuizSessionDTO.getRoomId(),participantQuizSessionDTO.getQuizId());
        }

        log.debug("Participant quiz session {} successfully validated for user {}.", sessionId, userId);
    }

    public void validateSubmissionStatus(UUID roomId,UUID quizId,UUID questionId,UUID userId){
        log.debug("Validating submission status: roomId={}, quizId={}, questionId={}, userId={}",
                    roomId, quizId, questionId, userId);

        Boolean isSubmitted=submissionDataService.isQuestionSubmitted(roomId,quizId,questionId,userId);

            if(isSubmitted){
                log.warn("Duplicate submission attempt: questionId={}, userId={}", questionId, userId);

                String msg=String.format("Answer for question %s is already submitted.", questionId);
                throw new InvalidSubmissionException(msg);
        }
    }

}