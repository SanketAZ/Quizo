package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sxy.optimus.dto.question.*;
import org.sxy.optimus.module.Question;
import org.sxy.optimus.projection.QuestionWithOptionsProjection;

import java.util.List;

@Mapper(componentModel="spring",uses = {OptionMapper.class})
public interface QuestionMapper{

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    Question toQuestion(QuestionRequestDTO questionRequestDTO);
    List<Question> toQuestionList(List<QuestionRequestDTO> questionRequestDTOList);

    QuestionCreateResDTO toQuestionCreateResDTO(Question question);
    List<QuestionCreateResDTO> toQuestionCreateResDTOList(List<Question> questions);

    QuestionUpdateResDTO toQuestionUpdateResDTO(Question question);

    QuestionDTO toQuestionDTO(QuestionWithOptionsProjection question);

    QuestionCacheDTO toQuestionCacheDTO(Question question);
}
