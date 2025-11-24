package org.sxy.frontier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.mapper.ParticipantQuizSessionMapper;
import org.sxy.frontier.module.ParticipantQuizSession;
import org.sxy.frontier.redis.dto.ParticipantQuizSessionCacheDTO;
import org.sxy.frontier.redis.repo.ParticipantQuizSessionCacheRepo;
import org.sxy.frontier.redis.service.ParticipantQuizSessionCacheService;
import org.sxy.frontier.repo.ParticipantQuizSessionRepo;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipantQuizSessionDataService {
    private static final Logger log = LoggerFactory.getLogger(ParticipantQuizSessionDataService.class);

    @Autowired
    private ParticipantQuizSessionRepo participantQuizSessionRepo;

    @Autowired
    private ParticipantQuizSessionMapper participantQuizSessionMapper;

    @Autowired
    private ParticipantQuizSessionCacheService participantQuizSessionCacheService;

    @Autowired
    private Clock clock;

    public Optional<ParticipantQuizSessionDTO> getParticipantQuizSession(UUID sessionId){
        Optional<ParticipantQuizSessionCacheDTO> cachedSessionOp=participantQuizSessionCacheService.getParticipantQuizSessionCache(sessionId);

        if(cachedSessionOp.isPresent()){
            log.debug("Cache HIT for sessionId={}", sessionId);
            return Optional.of(participantQuizSessionMapper.toParticipantQuizSessionDTO(cachedSessionOp.get()));
        }
        log.debug("Cache MISS for sessionId={}, loading from DB", sessionId);

        Optional<ParticipantQuizSession> participantQuizSessionOp=participantQuizSessionRepo.findById(sessionId);
        if(participantQuizSessionOp.isEmpty()){
            log.warn("Session not found in DB: sessionId={}",
                    sessionId);
            return Optional.empty();
        }

        //warm cache
        ParticipantQuizSession sessionEntity=participantQuizSessionOp.get();
        ParticipantQuizSessionDTO sessionDTO=participantQuizSessionMapper.toParticipantQuizSessionDTO(sessionEntity);
        participantQuizSessionCacheService.cacheParticipantQuizSession(sessionDTO);

        return Optional.of(sessionDTO);
    }
}