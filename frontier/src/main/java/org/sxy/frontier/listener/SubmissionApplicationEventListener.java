package org.sxy.frontier.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.dto.SubmissionDTO;
import org.sxy.frontier.event.SubmissionCachedEvent;
import org.sxy.frontier.redis.service.SubmissionCacheService;

import java.util.UUID;

@Component
public class SubmissionApplicationEventListener {

    private static final Logger log = LoggerFactory.getLogger(SubmissionApplicationEventListener.class);
    private final SubmissionCacheService submissionCacheService;

    @Autowired
    public SubmissionApplicationEventListener(SubmissionCacheService submissionCacheService) {
        this.submissionCacheService = submissionCacheService;
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onSubmissionCachedEvent(SubmissionCachedEvent submissionCachedEvent) {
        SubmissionDTO submissionDTO =submissionCachedEvent.submissionDTO();
        UUID userId = submissionDTO.getUserId();
        UUID quizId = submissionDTO.getQuizId();
        UUID roomId = submissionDTO.getRoomId();
        UUID questionId=submissionDTO.getQuestionId();

        log.info("Processing SubmissionCachedEvent - userId={}, quizId={}, roomId={}, questionId={}",
                userId, quizId, roomId, questionId);
        try {
            submissionCacheService.cacheSubmission(submissionDTO);
        } catch (Exception ex) {
            log.error("Failed to cache submission - userId={}, quizId={}, roomId={}, questionId={}, error={}",
                    userId, quizId, roomId, questionId, ex.getMessage(), ex);

        }
    }
}
