package org.sxy.frontier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.redis.QuizCacheRepo;

import java.util.UUID;

@Service
public class ParticipantSessionService {
    @Autowired
    private QuizCacheRepo quizCacheRepo;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AccessControlService accessControlService;

    public ActiveQuizQuestionDTO fetchQuestion(UUID roomId,UUID quizId,UUID userId,int qIndex,String sequence){
        accessControlService.validateQuizLive(quizId,roomId);
        accessControlService.validateUserInRoom(roomId,userId);
        UUID questionId=accessControlService.validateQIndex(roomId,quizId,qIndex,"A");
        return quizService.fetchActiveQuizQuestion(roomId,quizId,questionId);
    }

}
