package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.mapper.SubmissionMapper;
import org.sxy.frontier.redis.QuizCacheRepo;

import java.time.Clock;
import java.time.Instant;
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

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private Clock clock;

    public ActiveQuizQuestionDTO fetchQuestion(UUID roomId,UUID quizId,UUID userId,int qIndex,String sequence){
        log.info("Fetching question started: roomId={}, quizId={}, userId={}, qIndex={}, sequence={}",
                roomId, quizId, userId, qIndex, sequence);

        accessControlService.validateQuizLive(quizId,roomId);
        accessControlService.validateUserInRoom(roomId,userId);
        UUID questionId=accessControlService.validateQIndex(roomId,quizId,qIndex,"A");

        log.info("Successfully fetched question: quizId={}, roomId={}, questionId={}", quizId, roomId, questionId);

        return quizService.fetchActiveQuizQuestion(roomId,quizId,questionId);
    }

    public AnswerSubmissionResDTO submitQuestion(UUID roomId, UUID quizId, UUID userId, AnswerSubmissionReqDTO answerSubmissionReqDTO){
        log.info("Submit answer request: roomId={}, quizId={}, userId={}, questionId={}",
                roomId, quizId, userId, answerSubmissionReqDTO.getQuestionId());

        Instant submittedAt = Instant.now(clock);
        accessControlService.validateQuizLive(quizId,roomId);
        accessControlService.validateUserInRoom(roomId,userId);
        AnswerEvaluation res = quizService.evaluateAnswer(roomId,quizId,answerSubmissionReqDTO);
        submissionService.saveSubmission(userId,roomId,quizId,submittedAt,res);

        log.info("Answer evaluated successfully for roomId={}, quizId={}, userId={}, questionId={}",
                roomId, quizId, userId, res.getQuestionId());

        return submissionMapper.toAnswerSubmissionResDTO(res);
    }

}