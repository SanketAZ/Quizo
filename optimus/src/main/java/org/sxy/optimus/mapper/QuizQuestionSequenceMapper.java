package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.module.QuizQuestionSequence;

@Mapper(componentModel = "spring")
public interface QuizQuestionSequenceMapper {

    QuestionPositionDTO toQuestionPositionDTO(QuizQuestionSequence quizQuestionSequence);
}
