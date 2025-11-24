package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.sxy.optimus.dto.option.*;
import org.sxy.optimus.module.Option;
import org.sxy.optimus.projection.QuestionWithOptionsProjection;
import org.sxy.optimus.redis.dto.OptionCacheDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    Option toOption(OptionRequestDTO option);
    List<Option> toOptions(List<OptionRequestDTO> options);

    OptionCreateResDTO toOptionCreateResDTO(Option option);
    List<OptionCreateResDTO> toOptionCreateResDTOs(List<Option> options);

    OptionRequestDTO toOptionRequestDTO(OptionUpdateReqDTO option);
    List<OptionRequestDTO> toOptionRequestDTOs(List<OptionUpdateReqDTO> options);

    Option toOption(OptionUpdateReqDTO option);
    List<Option> toOptionsFromOptionUpdateReqDTO(List<OptionUpdateReqDTO> options);

    OptionDTO toOptionDTO(QuestionWithOptionsProjection.OptionProjection optionProjection);

    OptionCacheDTO toOptionCacheDTO(Option option);
}
