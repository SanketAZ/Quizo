package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.redis.QuizCacheRepo;

import java.util.UUID;

@Service
public class ParticipantSessionService {
    private static final Logger log = LoggerFactory.getLogger(ParticipantSessionService.class);
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

    public AnswerSubmissionResDTO submitQuestion(UUID roomId, UUID quizId, UUID userId, AnswerSubmissionReqDTO answerSubmissionReqDTO){
        log.info("Submit answer request: roomId={}, quizId={}, userId={}, questionId={}",
                roomId, quizId, userId, answerSubmissionReqDTO.getQuestionId());

        accessControlService.validateQuizLive(quizId,roomId);
        accessControlService.validateUserInRoom(roomId,userId);
        AnswerSubmissionResDTO res = quizService.evaluateAnswer(roomId,quizId,answerSubmissionReqDTO);

        log.info("Answer evaluated successfully for roomId={}, quizId={}, userId={}, questionId={}",
                roomId, quizId, userId, answerSubmissionReqDTO.getQuestionId());
        return res;
    }

}
