package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.event.ParticipantQuizSessionCachedEvent;
import org.sxy.frontier.mapper.SubmissionMapper;
import org.sxy.frontier.redis.repo.QuizCacheRepo;
import org.sxy.frontier.repo.ParticipantQuizSessionRepo;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipantSessionService {
    private static final Logger log = LoggerFactory.getLogger(ParticipantSessionService.class);
    @Autowired
    private QuizCacheRepo quizCacheRepo;

    @Autowired
    private ParticipantQuizSessionRepo participantQuizSessionRepo;

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

    @Autowired
    private ParticipantQuizSessionService participantQuizSessionService;

    @Autowired
    ApplicationEventPublisher publisher;
    @Autowired
    private QuizDataService quizDataService;

    public ActiveQuizQuestionDTO fetchQuestion(UUID roomId,UUID quizId,UUID userId,int qIndex,String sequence){
        log.info("Fetching question started: roomId={}, quizId={}, userId={}, qIndex={}, sequence={}",
                roomId, quizId, userId, qIndex, sequence);

        accessControlService.validateQuizLive(quizId,roomId);
        accessControlService.validateUserInRoom(roomId,userId);

        UUID questionId= quizDataService.getQuestionId(roomId,quizId,qIndex,sequence);

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

    public ParticipantQuizSessionDTO startQuiz(UUID roomId,UUID quizId,UUID userId){
        log.info("Start quiz request received: roomId={}, quizId={}, userId={}",
                roomId, quizId, userId);

        accessControlService.validateQuizLive(quizId,roomId);
        accessControlService.validateUserInRoom(roomId,userId);

        //checking is there any existing session
        Optional<ParticipantQuizSessionDTO> existingSessionOp=participantQuizSessionService.getParticipantQuizSession(roomId,quizId,userId);
        if(existingSessionOp.isPresent()){
            publisher.publishEvent(new ParticipantQuizSessionCachedEvent(existingSessionOp.get()));
            return existingSessionOp.get();
        }

        return participantQuizSessionService.createParticipantQuizSession(roomId, quizId, userId, "STARTED");
    }

}