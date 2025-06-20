package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sxy.optimus.dto.QuestionCreateResDTO;
import org.sxy.optimus.dto.QuestionRequestDTO;
import org.sxy.optimus.module.Question;

import java.util.List;

@Mapper(componentModel="spring",uses = {OptionMapper.class})
public interface QuestionMapper{

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    Question toQuestion(QuestionRequestDTO questionRequestDTO);
    List<Question> toQuestionList(List<QuestionRequestDTO> questionRequestDTOList);

    QuestionCreateResDTO toQuestionCreateResDTO(Question question);
    List<QuestionCreateResDTO> toQuestionCreateResDTOList(List<Question> questions);
}
