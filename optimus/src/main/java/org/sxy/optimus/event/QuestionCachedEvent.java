package org.sxy.optimus.event;

import org.sxy.optimus.dto.question.QuestionDTO;
import org.sxy.optimus.redis.dto.QuestionCacheDTO;

import java.time.Duration;
import java.util.UUID;

public record QuestionCachedEvent(UUID roomId, UUID quizId, QuestionDTO questionDTO) {
}