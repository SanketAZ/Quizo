package org.sxy.frontier.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.module.ParticipantQuizSession;
import org.sxy.frontier.redis.dto.ParticipantQuizSessionCacheDTO;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface ParticipantQuizSessionMapper {
    ParticipantQuizSessionMapper INSTANCE = Mappers.getMapper(ParticipantQuizSessionMapper.class);

    @Mapping(target = "startTime",source = "startTime", qualifiedByName = "instantToLong")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "instantToLong")
    @Mapping(target = "finalEndTime",source = "finalEndTime", qualifiedByName = "instantToLong")
    ParticipantQuizSessionCacheDTO toParticipantQuizSessionCacheDTO(ParticipantQuizSession participantQuizSession);

    @Mapping(target = "startTime",source = "startTime", qualifiedByName = "longToInstant")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "longToInstant")
    @Mapping(target = "finalEndTime",source = "finalEndTime", qualifiedByName = "longToInstant")
    ParticipantQuizSession toParticipantQuizSession(ParticipantQuizSessionCacheDTO participantQuizSessionCacheDTO);

    @Mapping(target = "startTime",source = "startTime", qualifiedByName = "longToInstant")
    @Mapping(target = "endTime", source = "endTime",qualifiedByName = "longToInstant")
    @Mapping(target = "finalEndTime", source = "finalEndTime",qualifiedByName = "longToInstant")
    ParticipantQuizSessionDTO toParticipantQuizSessionDTO(ParticipantQuizSessionCacheDTO participantQuizSessionCacheDTO);

    ParticipantQuizSessionDTO toParticipantQuizSessionDTO(ParticipantQuizSession participantQuizSession);

    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "instantToLong")
    @Mapping(target = "endTime",source = "endTime",qualifiedByName = "instantToLong")
    @Mapping(target = "finalEndTime",source = "finalEndTime", qualifiedByName = "instantToLong")
    ParticipantQuizSessionCacheDTO toParticipantQuizSessionCacheDTO(ParticipantQuizSessionDTO participantQuizSessionDTO);


    @Named("longToInstant")
    default Instant longToInstant(Long epochMilli) {
        return epochMilli != null ? Instant.ofEpochMilli(epochMilli) : null;
    }

    @Named("instantToLong")
    default Long instantToLong(Instant time) {
        return time != null ? time.toEpochMilli() : null;
    }
}