package org.sxy.optimus.event;

import org.sxy.optimus.dto.question.QuestionPositionDTO;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public record QuizQuestionSequenceCachedEvent(List<QuestionPositionDTO> questionPositions, String seqLabel, UUID quizId, UUID roomId,
                                              Duration ttl) {
}
