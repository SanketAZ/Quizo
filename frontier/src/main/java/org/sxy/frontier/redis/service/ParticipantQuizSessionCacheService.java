package org.sxy.frontier.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.mapper.ParticipantQuizSessionMapper;
import org.sxy.frontier.module.ParticipantQuizSession;
import org.sxy.frontier.redis.dto.ParticipantQuizSessionCacheDTO;
import org.sxy.frontier.redis.repo.ParticipantQuizSessionCacheRepo;
import org.sxy.frontier.repo.ParticipantQuizSessionRepo;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipantQuizSessionCacheService {

    private static final Logger log = LoggerFactory.getLogger(ParticipantQuizSessionCacheService.class);

    private static final Duration MIN_TTL= Duration.ofMinutes(5);
    private static final Duration MAX_TTL= Duration.ofMinutes(60);
    private static final Duration TTL_BUFFER= Duration.ofMinutes(10);
    private static final Duration DEFAULT_TTL= Duration.ofMinutes(60);

    @Autowired
    private ParticipantQuizSessionCacheRepo participantQuizSessionCacheRepo;

    @Autowired
    private ParticipantQuizSessionRepo participantQuizSessionRepo;

    @Autowired
    private ParticipantQuizSessionMapper participantQuizSessionMapper;

    @Autowired
    private Clock clock;

    public Optional<ParticipantQuizSessionCacheDTO> getParticipantQuizSessionCache(UUID sessionId){
        return participantQuizSessionCacheRepo.getParticipantQuizSessionCache(sessionId);
    }

    public ParticipantQuizSessionCacheDTO cacheParticipantQuizSession(ParticipantQuizSessionDTO sessionDTO){
        log.debug("Caching participant quiz session: sessionId={}", sessionDTO.getSessionId());

        ParticipantQuizSessionCacheDTO cacheDTO=participantQuizSessionMapper.toParticipantQuizSessionCacheDTO(sessionDTO);
        Duration ttl=calculateTTLDuration(sessionDTO);
        try {
            participantQuizSessionCacheRepo.saveParticipantQuizSessionCache(cacheDTO,ttl);

            log.debug("Successfully cached session: sessionId={}, ttl={}",
                    sessionDTO.getSessionId(), ttl);
        }catch (Exception e){
            log.error("Failed to cache session: sessionId={}",
                    sessionDTO.getSessionId(), e);
        }
        return cacheDTO;
    }

    public void invalidateParticipantQuizSessionCache(UUID sessionId){
        log.debug("Invalidating cache for sessionId={}", sessionId);

        try {
            participantQuizSessionCacheRepo.deleteParticipantQuizSessionCache(sessionId);
            log.debug("Successfully invalidated cache for sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("Failed to invalidate cache for sessionId={}", sessionId, e);
        }
    }

    public boolean existsParticipantQuizSessionCache(UUID sessionId) {
        return participantQuizSessionCacheRepo.existsParticipantQuizSessionCache(sessionId);
    }

    private ParticipantQuizSessionCacheDTO warmCacheParticipantQuizSession(ParticipantQuizSessionDTO sessionDTO){
        return cacheParticipantQuizSession(sessionDTO);
    }

    public Duration calculateTTLDuration(ParticipantQuizSessionDTO sessionDTO){
        Instant currentTime = Instant.now(clock);
        Instant endTime = sessionDTO.getFinalEndTime();

        if (endTime == null) {
            log.warn("Session {} has null finalEndTime, using default TTL", sessionDTO.getSessionId());
            return DEFAULT_TTL;
        }

        Duration remaining=Duration.between(currentTime,endTime);
        if (remaining.isNegative() || remaining.isZero()) {
            return MIN_TTL;
        }

        Duration ttl = remaining.plus(TTL_BUFFER);

        Duration maxTtl = MAX_TTL;
        if (ttl.compareTo(maxTtl) > 0) {
            ttl = maxTtl;
        }
        return ttl;
    }
}
