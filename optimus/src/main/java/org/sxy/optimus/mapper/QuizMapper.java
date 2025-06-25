package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sxy.optimus.dto.quiz.QuizCreateDTO;
import org.sxy.optimus.dto.quiz.QuizCreatedDTO;
import org.sxy.optimus.dto.quiz.QuizUpdateResponseDTO;
import org.sxy.optimus.module.Quiz;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    Quiz toQuiz(QuizCreateDTO quizCreateDTO);

    QuizCreatedDTO toQuizCreateDTO(Quiz quiz);

    QuizUpdateResponseDTO toQuizUpdateResponseDTO(Quiz quiz);
}
