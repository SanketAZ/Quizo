package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.dto.QuizDetailDTO;
import org.sxy.frontier.event.ParticipantQuizSessionCachedEvent;
import org.sxy.frontier.exception.ResourceDoesNotExitsException;
import org.sxy.frontier.mapper.ParticipantQuizSessionMapper;
import org.sxy.frontier.module.ParticipantQuizSession;
import org.sxy.frontier.repo.ParticipantQuizSessionRepo;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipantQuizSessionService {
    private static final Logger log = LoggerFactory.getLogger(ParticipantQuizSessionService.class);
    @Autowired
    private ParticipantQuizSessionRepo participantQuizSessionRepo;
    @Autowired
    private ParticipantQuizSessionDataService participantQuizSessionDataService;
    @Autowired
    private ParticipantQuizSessionMapper participantQuizSessionMapper;
    @Autowired
    private QuizDataService quizDataService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private Clock clock;

    private static final String DEFAULT_SEQUENCE_LABEL = "A";
    private static final int INITIAL_QUESTION_INDEX = 1;


    @Transactional
    public ParticipantQuizSessionDTO createParticipantQuizSession(UUID roomId, UUID quizId, UUID userId, String status){
        log.debug("Creating Participant Quiz session: roomId={}, quizId={}, userId={}, status={}",
                roomId, quizId, userId, status);

        Instant currentTime = Instant.now(clock);

        QuizDetailDTO quizDetail= quizDataService.getQuizDetail(roomId,quizId);
        Instant quizEndTime = quizDetail.getStartTime()
                .plus(Duration.ofSeconds(quizDetail.getDurationSec()));

        ParticipantQuizSession participantQuizSession=ParticipantQuizSession.builder().roomId(roomId)
                .quizId(quizId)
                .userId(userId)
                .startTime(currentTime)
                .finalEndTime(quizEndTime)
                .sequenceLabel(DEFAULT_SEQUENCE_LABEL)
                .currentIndex(INITIAL_QUESTION_INDEX)
                .status(status)
                .build();

        ParticipantQuizSession savedEntity=participantQuizSessionRepo.save(participantQuizSession);
        ParticipantQuizSessionDTO sessionDTO=participantQuizSessionMapper.toParticipantQuizSessionDTO(savedEntity);

        log.info("Participant Quiz session created: sessionId={}, userId={}, quizId={}",
                savedEntity.getSessionId(), userId, quizId);

        publisher.publishEvent(new ParticipantQuizSessionCachedEvent(sessionDTO));
        return sessionDTO;
    }

    public ParticipantQuizSessionDTO getParticipantQuizSession(UUID sessionId){
        Optional<ParticipantQuizSessionDTO> cachedOp=participantQuizSessionDataService.getParticipantQuizSession(sessionId);

        if (cachedOp.isEmpty()) {
            log.warn("Session not found: sessionId={}", sessionId);
            throw new ResourceDoesNotExitsException("Participant Quiz session not found for id: " + sessionId);
        }

        return cachedOp.get();
    }

    public Optional<ParticipantQuizSessionDTO> getParticipantQuizSession(UUID roomId, UUID quizId, UUID userId) {

        Optional<ParticipantQuizSession> existingSessionOp=participantQuizSessionRepo.findByRoomIdAndQuizIdAndUserId(roomId,quizId,userId);

        if(existingSessionOp.isEmpty()){
            log.info("Participant Quiz Session does not found in DB: userId={}, quizId={}", userId, quizId);
            return Optional.empty();
        }

        ParticipantQuizSessionDTO sessionDTO=participantQuizSessionMapper
                .toParticipantQuizSessionDTO(existingSessionOp.get());
        log.debug("Participant Quiz Session  found in DB: userId={}, quizId={}", userId, quizId);
        return Optional.of(sessionDTO);
    }
}

