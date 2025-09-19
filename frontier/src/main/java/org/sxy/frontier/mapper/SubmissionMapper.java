package org.sxy.frontier.mapper;

import org.mapstruct.Mapper;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    AnswerSubmissionResDTO toAnswerSubmissionResDTO(AnswerEvaluation answerEvaluation);
}
