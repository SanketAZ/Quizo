package org.sxy.frontier.mapper;

import org.mapstruct.Mapper;
import org.sxy.frontier.dto.AnswerEvaluation;
import org.sxy.frontier.dto.SubmissionDTO;
import org.sxy.frontier.dto.question.AnswerSubmissionResDTO;
import org.sxy.frontier.module.Submission;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.SubmissionCacheDTO;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    AnswerSubmissionResDTO toAnswerSubmissionResDTO(AnswerEvaluation answerEvaluation);
    SubmissionCacheDTO toSubmissionCacheDTO(SubmissionDTO submissionDTO);
    SubmissionDTO toSubmissionDTO(SubmissionCacheDTO submissionCacheDTO);
    SubmissionDTO toSubmissionDTO(Submission submission);
}
