package org.sxy.frontier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.client.OptimusServiceClient;
import org.sxy.frontier.dto.question.QuestionPositionDTO;
import org.sxy.frontier.exception.QuizNotActiveException;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.redis.QuizCacheRepo;
import org.sxy.frontier.redis.RoomCacheRepo;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccessControlService {

    @Autowired
    private QuizCacheRepo quizCacheRepo;
    @Autowired
    private RoomCacheRepo roomCacheRepo;
    @Autowired
    private OptimusServiceClient  optimusServiceClient;

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

}
