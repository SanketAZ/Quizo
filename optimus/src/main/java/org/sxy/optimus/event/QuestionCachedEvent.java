package org.sxy.optimus.event;

import org.sxy.optimus.dto.question.QuestionCacheDTO;

import java.time.Duration;
import java.util.UUID;

public record QuestionCachedEvent(UUID roomId, UUID quizId, QuestionCacheDTO questionCacheDTO, Duration ttl) {
}