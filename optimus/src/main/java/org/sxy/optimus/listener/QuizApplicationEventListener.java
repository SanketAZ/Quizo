package org.sxy.optimus.listener;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.sxy.optimus.event.QuestionCachedEvent;
import org.sxy.optimus.event.QuizDetailCachedEvent;
import org.sxy.optimus.event.QuizQuestionSequenceCachedEvent;
import org.sxy.optimus.redis.repo.QuizCacheRepository;
import org.sxy.optimus.redis.service.QuizCacheService;

import java.util.List;

@Component
public class QuizApplicationEventListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(QuizApplicationEventListener.class);
    private final QuizCacheService quizCacheService;

    @Autowired
    public QuizApplicationEventListener(QuizCacheService quizCacheService) {
        this.quizCacheService = quizCacheService;
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onQuizDetailCache(QuizDetailCachedEvent evt) {
        try {
            quizCacheService.cacheQuizDetail(evt.dto());
        } catch (Exception e) {
            log.warn("Redis upload failed (quiz details) quizId={} roomId={} err={}",
                    evt.dto().getQuizId(), evt.dto().getRoomId(), e.toString(), e);
        }
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onQuestionCache(QuestionCachedEvent evt) {
        try {
            quizCacheService.cacheQuestion(evt.questionDTO(),evt.quizId(),evt.roomId());
        } catch (Exception e) {
            log.warn("Redis upload failed (question) roomId={} quizId={} questionId={} err={}",
                    evt.roomId(), evt.quizId(), evt.questionDTO().getQuestionId(), e.toString(), e);
        }
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onQuizQuestionSequenceCachedEvent(QuizQuestionSequenceCachedEvent evt) {
        try {
            quizCacheService.cacheQuizQuestionSequence(evt.questionPositions(),evt.seqLabel(),evt.quizId(),evt.roomId());
        } catch (Exception e) {
            log.warn("Redis upload failed (question positions) roomId={} quizId={} err={}",
                    evt.roomId(), evt.quizId(),e.toString(), e);
        }
    }
}
