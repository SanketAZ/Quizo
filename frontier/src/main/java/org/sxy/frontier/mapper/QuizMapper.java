package org.sxy.frontier.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.sxy.frontier.dto.QuizDetailDTO;
import org.sxy.frontier.dto.question.ActiveQuizQuestionDTO;
import org.sxy.frontier.dto.question.QuestionDTO;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    ActiveQuizQuestionDTO toActiveQuizQuestionDTO(QuestionCacheDTO questionCacheDTO);
    ActiveQuizQuestionDTO toActiveQuizQuestionDTO(QuestionDTO questionDTO);
    QuestionDTO toQuestionDTO(QuestionCacheDTO questionCacheDTO);

    @Mapping(target = "startTime",source = "startTime",qualifiedByName = "longToInstant")
    QuizDetailDTO toQuizDetailDTO(QuizDetailCacheDTO questionCacheDTO);

    @Mapping(target = "startTime",source = "startTime",qualifiedByName = "instantToLong")
    QuizDetailCacheDTO toQuizDetailCacheDTO(QuizDetailDTO quizDetailDTO);

    @Named("longToInstant")
    default Instant longToInstant(Long epochMilli) {
        return epochMilli != null ? Instant.ofEpochMilli(epochMilli) : null;
    }

    @Named("instantToLong")
    default Long instantToLong(Instant time) {
        return time != null ? time.toEpochMilli() : null;
    }

}
