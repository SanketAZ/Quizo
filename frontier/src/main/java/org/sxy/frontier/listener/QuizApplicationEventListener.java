package org.sxy.frontier.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.event.ParticipantQuizSessionCachedEvent;
import org.sxy.frontier.mapper.ParticipantQuizSessionMapper;
import org.sxy.frontier.module.ParticipantQuizSession;
import org.sxy.frontier.redis.repo.ParticipantQuizSessionCacheRepo;
import org.sxy.frontier.redis.dto.ParticipantQuizSessionCacheDTO;
import org.sxy.frontier.redis.service.ParticipantQuizSessionCacheService;
import org.sxy.frontier.service.ParticipantQuizSessionService;

import java.time.Duration;
import java.util.UUID;

@Component
public class QuizApplicationEventListener {
    private static final Logger log = LoggerFactory.getLogger(QuizApplicationEventListener.class);
    @Autowired
    private ParticipantQuizSessionMapper participantQuizSessionMapper;
    @Autowired
    private ParticipantQuizSessionService participantQuizSessionService;
    @Autowired
    private ParticipantQuizSessionCacheService participantQuizSessionCacheService;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("cacheWriterExecutor")
    public void onParticipantQuizSessionCreated(ParticipantQuizSessionCachedEvent event) {
        ParticipantQuizSessionDTO sessionDTO = event.sessionDTO();

        UUID sessionId = sessionDTO.getSessionId();
        UUID userId = sessionDTO.getUserId();
        UUID quizId = sessionDTO.getQuizId();

        try {
            log.info("Processing ParticipantQuizSessionCachedEvent: sessionId={}, userId={}, quizId={}",
                    sessionId, userId, quizId);
            participantQuizSessionCacheService.cacheParticipantQuizSession(sessionDTO);

        } catch (Exception e) {

            log.error(
                    "Event processing FAILED: sessionId={}, userId={}, quizId={}, error={}",
                    sessionId, userId, quizId,
                    e.getMessage(), e);
        }
    }
}
