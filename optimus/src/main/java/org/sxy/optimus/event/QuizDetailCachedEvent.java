package org.sxy.optimus.event;

import org.sxy.optimus.dto.quiz.QuizDetailDTO;
import org.sxy.optimus.redis.dto.QuizDetailCacheDTO;

public record QuizDetailCachedEvent(QuizDetailDTO dto, long ttl) {
}
