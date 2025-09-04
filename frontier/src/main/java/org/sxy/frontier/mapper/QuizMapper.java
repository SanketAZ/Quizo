package org.sxy.frontier.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    ActiveQuizQuestionDTO toActiveQuizQuestionDTO(QuestionCacheDTO questionCacheDTO);
    QuestionDTO toQuestionDTO(QuestionCacheDTO questionCacheDTO);
}
