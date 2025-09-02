package org.sxy.frontier.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sxy.frontier.dto.option.ActiveQuizOptionDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.redis.dto.OptionCacheDTO;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    OptionMapper INSTANCE = Mappers.getMapper(OptionMapper.class);
    ActiveQuizOptionDTO toActiveQuizOptionDTO(OptionCacheDTO optionCacheDTO);
}
