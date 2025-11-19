package org.sxy.frontier.redis.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.sxy.frontier.redis.dto.ParticipantQuizSessionCacheDTO;
import org.sxy.frontier.utility.RedisKeys;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ParticipantQuizSessionCacheRepo {
    private final RedisTemplate<String, ParticipantQuizSessionCacheDTO> rtParticipantQuizSessionCache;
    private final ValueOperations<String, ParticipantQuizSessionCacheDTO> valueOpsParticipantQuizSessionCache;


    @Autowired
    public ParticipantQuizSessionCacheRepo(@Qualifier("String-ParticipantQuizSessionCache") RedisTemplate<String, ParticipantQuizSessionCacheDTO> rtParticipantQuizSessionCacheDTO) {
        this.rtParticipantQuizSessionCache = rtParticipantQuizSessionCacheDTO;
        this.valueOpsParticipantQuizSessionCache = rtParticipantQuizSessionCacheDTO.opsForValue();
    }

    public void saveParticipantQuizSessionCache(ParticipantQuizSessionCacheDTO participantQuizSessionCacheDTO, Duration ttl) {
        String sessionId= participantQuizSessionCacheDTO.getSessionId();
        String key= RedisKeys.buildParticipantQuizSessionKey(sessionId);
        valueOpsParticipantQuizSessionCache.set(key, participantQuizSessionCacheDTO,ttl);
    }

    public Optional<ParticipantQuizSessionCacheDTO> getParticipantQuizSessionCache(UUID sessionId) {
        String sId= sessionId.toString();
        String key= RedisKeys.buildParticipantQuizSessionKey(sId);
        return Optional.ofNullable(valueOpsParticipantQuizSessionCache.get(key));
    }

    public void deleteParticipantQuizSessionCache(UUID sessionId) {
        String sessionID= sessionId.toString();
        String key= RedisKeys.buildParticipantQuizSessionKey(sessionID);
        valueOpsParticipantQuizSessionCache.getAndDelete(key);
    }

    public boolean existsParticipantQuizSessionCache(UUID sessionId) {
        String sessionID= sessionId.toString();
        String key= RedisKeys.buildParticipantQuizSessionKey(sessionID);
        return valueOpsParticipantQuizSessionCache.get(key) != null;
    }
}
