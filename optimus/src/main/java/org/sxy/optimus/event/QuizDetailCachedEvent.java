package org.sxy.optimus.event;

import org.sxy.optimus.dto.quiz.QuizDetailCacheDTO;

public record QuizDetailCachedEvent(QuizDetailCacheDTO dto, long ttl) {
}
