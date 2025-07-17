package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sxy.optimus.dto.quiz.*;
import org.sxy.optimus.module.Quiz;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    Quiz toQuiz(QuizCreateDTO quizCreateDTO);

    QuizCreatedDTO toQuizCreateDTO(Quiz quiz);

    QuizUpdateResponseDTO toQuizUpdateResponseDTO(Quiz quiz);

    QuizDisplayDTO quizToQuizDisplayDTO(Quiz quiz);

    QuizDetailCacheDTO toQuizDetailCacheDTO(Quiz quiz);
}
