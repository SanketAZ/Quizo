package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.dto.question.QuestionPositionDTO;
import org.sxy.frontier.exception.QuizNotActiveException;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.exception.UnauthorizedActionException;
import org.sxy.frontier.mapper.ParticipantQuizSessionMapper;
import org.sxy.frontier.redis.repo.ParticipantQuizSessionCacheRepo;
import org.sxy.frontier.redis.repo.QuizCacheRepo;
import org.sxy.frontier.redis.repo.RoomCacheRepo;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;
import org.sxy.frontier.repo.ParticipantQuizSessionRepo;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccessControlService {

    private static final Logger log = LoggerFactory.getLogger(AccessControlService.class);
    @Autowired
    private QuizCacheRepo quizCacheRepo;
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

    public UUID validateQIndex(UUID roomId,UUID quizId,Integer index,String seqLabel){
        if(quizCacheRepo.isQuestionIndexPresent(roomId,quizId,index))
            return UUID.fromString(quizCacheRepo.getQuestionIdFromIndex(roomId,quizId,index));
        List<QuestionPositionDTO> quePositions=optimusServiceClient.getQuestionPositions(roomId,quizId,seqLabel);
        Map<Integer,String>quePositionsMap=quePositions
                    .stream()
                    .collect(Collectors.toMap(QuestionPositionDTO::getPosition,QuestionPositionDTO::getQuestionId));

        if(!quePositionsMap.containsKey(index)){
            String msg=String.format("Quiz Index %s is not in quiz %s",index,quizId);
            throw new ResourceDoesNotExitsException(msg);
        }
        return UUID.fromString(quePositionsMap.get(index));
    }

    public void validateQuizLive(UUID quizId,UUID roomId){
        Optional<QuizDetailCacheDTO> quizDetailOpt=quizCacheRepo.getQuizDetails(roomId,quizId);
        var quizDetail = quizDetailOpt.orElseGet(
                () -> optimusServiceClient.getQuizDetails(roomId, quizId));

        Instant currentTime=Instant.now();
        Instant startTime=Instant.parse(quizDetail.getStartTime());
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

}