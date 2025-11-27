package org.sxy.frontier.redis.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.sxy.frontier.redis.dto.RoomUserDetailsCache;
import org.sxy.frontier.redis.dto.SubmissionCacheDTO;
import org.sxy.frontier.utility.RedisKeys;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SubmissionCacheRepo {

    private final RedisTemplate<String, SubmissionCacheDTO> rtSubmissionCache;
    private final ValueOperations<String, SubmissionCacheDTO> valueOpsSubmissionCache;

    @Autowired
    public SubmissionCacheRepo(@Qualifier("String-SubmissionCacheDTO") RedisTemplate<String, SubmissionCacheDTO> rtSubmissionCache) {
        this.rtSubmissionCache = rtSubmissionCache;
        this.valueOpsSubmissionCache = rtSubmissionCache.opsForValue();
    }

    public void saveSubmissionCache(SubmissionCacheDTO submissionCacheDTO, Duration ttl) {
        String roomId = submissionCacheDTO.getRoomId();
        String quizId = submissionCacheDTO.getQuizId();
        String userId = submissionCacheDTO.getUserId();
        String questionId = submissionCacheDTO.getQuestionId();
        String key= RedisKeys.buildSubmissionKey(roomId,quizId,questionId,userId);
        valueOpsSubmissionCache.set(key,submissionCacheDTO,ttl);
    }

    public Optional<SubmissionCacheDTO> getSubmissionCache(String roomId, String quizId, String userId, String questionId) {
        String key= RedisKeys.buildSubmissionKey(roomId,quizId,questionId,userId);
        SubmissionCacheDTO submissionCacheDTO=valueOpsSubmissionCache.get(key);
        return Optional.ofNullable(submissionCacheDTO);
    }

}