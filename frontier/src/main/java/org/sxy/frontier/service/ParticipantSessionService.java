package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.dto.SubmissionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionReqDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.event.ParticipantQuizSessionCachedEvent;
import org.sxy.frontier.event.SubmissionCachedEvent;
import org.sxy.frontier.mapper.QuizMapper;
import org.sxy.frontier.mapper.SubmissionMapper;
import org.sxy.frontier.module.Submission;
import org.sxy.frontier.redis.dto.SubmissionCacheDTO;
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
    private SubmissionDataService submissionDataService;
    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private QuizMapper quizMapper;
    @Autowired
    private Clock clock;
    @Autowired
    private ParticipantQuizSessionService participantQuizSessionService;
    @Autowired
    ApplicationEventPublisher publisher;
    @Autowired
    private QuizDataService quizDataService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public ActiveQuizQuestionDTO fetchActiveQuizQuestion(UUID userId,UUID sessionId,int qIndex){
        log.info("Fetching question : sessionId={},qIndex={},userId={}", sessionId, qIndex,userId);

        accessControlService.validateParticipantQuizSession(sessionId,userId);
        ParticipantQuizSessionDTO participantQuizSession=participantQuizSessionService.getParticipantQuizSession(sessionId);
        UUID roomId=participantQuizSession.getRoomId();
        UUID quizId=participantQuizSession.getQuizId();
        String sequence=participantQuizSession.getSequenceLabel();

        UUID questionId= quizDataService.getQuestionId(roomId,quizId,qIndex,sequence);

        QuestionDTO questionDTO=quizService.getQuizQuestion(roomId,quizId,questionId);
        Optional<SubmissionDTO> submissionOp=submissionDataService.findSubmission(roomId,quizId,questionId,userId);

        ActiveQuizQuestionDTO activeQuizQuestionDTO=formActiveQuizQuestion(questionDTO,submissionOp);
        log.info("Successfully fetched question: quizId={}, roomId={}, questionId={}", quizId, roomId, questionId);

        return activeQuizQuestionDTO;
    }

    @Transactional
    public AnswerSubmissionResDTO submitQuestion(UUID userId, UUID sessionId,AnswerSubmissionReqDTO answerSubmissionReqDTO){
        log.info("Submit answer request: sessionId={}, userId={}, questionId={}",
                sessionId, userId, answerSubmissionReqDTO.getQuestionId());

        Instant submittedAt = Instant.now(clock);

        accessControlService.validateParticipantQuizSession(sessionId,userId);
        ParticipantQuizSessionDTO participantQuizSession=participantQuizSessionService.getParticipantQuizSession(sessionId);
        UUID roomId=participantQuizSession.getRoomId();
        UUID quizId=participantQuizSession.getQuizId();
        UUID questionId=UUID.fromString(answerSubmissionReqDTO.getQuestionId());
        accessControlService.validateSubmissionStatus(roomId,quizId,questionId,userId);

        AnswerEvaluation res = quizService.evaluateAnswer(roomId,quizId,answerSubmissionReqDTO);
        Submission submission=submissionService.saveSubmission(userId,roomId,quizId,submittedAt,res);
        SubmissionDTO submissionDTO=submissionMapper.toSubmissionDTO(submission);

        log.info("Answer evaluated successfully for roomId={}, quizId={}, userId={}, questionId={}",
                roomId, quizId, userId, res.getQuestionId());

        eventPublisher.publishEvent(new SubmissionCachedEvent(submissionDTO));
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

    private ActiveQuizQuestionDTO formActiveQuizQuestion(QuestionDTO questionDTO,Optional<SubmissionDTO>submissionOp){
        ActiveQuizQuestionDTO activeQuizQuestionDTO=quizMapper.toActiveQuizQuestionDTO(questionDTO);
        if(submissionOp.isEmpty()){
            activeQuizQuestionDTO.setAnswered(false);
            activeQuizQuestionDTO.setSelectedOptionId(null);
            return activeQuizQuestionDTO;
        }
        activeQuizQuestionDTO.setAnswered(true);
        activeQuizQuestionDTO.setSelectedOptionId(submissionOp.get().getSelectedOptionId());
        return activeQuizQuestionDTO;
    }

}