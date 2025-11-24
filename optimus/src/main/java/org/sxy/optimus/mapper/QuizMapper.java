package org.sxy.optimus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.sxy.optimus.dto.quiz.*;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.redis.dto.QuizDetailCacheDTO;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    Quiz toQuiz(QuizCreateDTO quizCreateDTO);

    QuizCreatedDTO toQuizCreateDTO(Quiz quiz);

    QuizUpdateResponseDTO toQuizUpdateResponseDTO(Quiz quiz);

    QuizDisplayDTO quizToQuizDisplayDTO(Quiz quiz);

    @Mapping(target = "startTime" ,source = "startTime" ,qualifiedByName ="instantToLong" )
    QuizDetailCacheDTO toQuizDetailCacheDTO(Quiz quiz);

    QuizStartTimeResDTO toQuizStartTimeResDTO(Quiz quiz);

    QuizDetailDTO toQuizDetailDTO(Quiz quiz);

    @Mapping(target = "startTime" ,source = "startTime" ,qualifiedByName ="instantToLong" )
    QuizDetailCacheDTO toQuizDetailCacheDTO(QuizDetailDTO quizDetailDTO);

    @Mapping(target = "startTime" ,source = "startTime" ,qualifiedByName ="longToInstant" )
    QuizDetailDTO toQuizDetailDTO(QuizDetailCacheDTO quizDetailCacheDTO);

    @Named("longToInstant")
    default Instant longToInstant(Long epochMilli) {
        return epochMilli != null ? Instant.ofEpochMilli(epochMilli) : null;
    }

    @Named("instantToLong")
    default Long instantToLong(Instant time) {
        return time != null ? time.toEpochMilli() : null;
    }
}
