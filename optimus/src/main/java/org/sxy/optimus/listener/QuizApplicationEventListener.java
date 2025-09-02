package org.sxy.optimus.listener;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.sxy.optimus.dto.quiz.QuizDetailCacheDTO;
import org.sxy.optimus.event.QuestionCachedEvent;
import org.sxy.optimus.event.QuizDetailCachedEvent;
import org.sxy.optimus.event.QuizQuestionSequenceCachedEvent;
import org.sxy.optimus.redis.RedisCacheQuizRepository;

import java.util.List;

@Component
public class QuizApplicationEventListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(QuizApplicationEventListener.class);
    private final RedisCacheQuizRepository redisCacheQuizRepository;

    @Autowired
    public QuizApplicationEventListener(RedisCacheQuizRepository redisCacheQuizRepository) {
        this.redisCacheQuizRepository = redisCacheQuizRepository;
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onQuizDetailCache(QuizDetailCachedEvent evt) {
        try {
            redisCacheQuizRepository.uploadQuizDetails(evt.dto(), evt.ttl());
        } catch (Exception e) {
            log.warn("Redis upload failed (quiz details) quizId={} roomId={} err={}",
                    evt.dto().getQuizId(), evt.dto().getRoomId(), e.toString(), e);
        }
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onQuestionCache(QuestionCachedEvent evt) {
        try {
            redisCacheQuizRepository.uploadQuizQuestions(List.of(evt.questionCacheDTO()),evt.quizId(),evt.roomId(),evt.ttl().toSeconds());
        } catch (Exception e) {
            log.warn("Redis upload failed (question) roomId={} quizId={} questionId={} err={}",
                    evt.roomId(), evt.quizId(), evt.questionCacheDTO().getQuestionId(), e.toString(), e);
        }
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onQuizQuestionSequenceCachedEvent(QuizQuestionSequenceCachedEvent evt) {
        try {
            redisCacheQuizRepository.uploadQuizQuestionSequence(evt.questionPositions(),evt.seqLabel(),evt.quizId(),evt.roomId(),evt.ttl().toSeconds());
        } catch (Exception e) {
            log.warn("Redis upload failed (question positions) roomId={} quizId={} err={}",
                    evt.roomId(), evt.quizId(),e.toString(), e);
        }
    }
}
