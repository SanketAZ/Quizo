package org.sxy.optimus.redis.repo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.sxy.optimus.dto.pojo.RoomUserDetails;
import org.sxy.optimus.utility.redis.RedisKeys;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Validated
public class RoomCacheRepository {

    private static final Logger logger = LoggerFactory.getLogger(RoomCacheRepository.class);

    private final RedisTemplate<String,RoomUserDetails> redisTemplateRoomUserDetails;

    public RoomCacheRepository(@Qualifier("String-RoomUserDetails")RedisTemplate<String, RoomUserDetails> roomUserDetails) {
        redisTemplateRoomUserDetails = roomUserDetails;
    }

    public void cacheRoomUserDetails(@NotEmpty List<RoomUserDetails> users, @NotNull UUID roomId, @Min(1) Long ttlInSeconds){
        String key= RedisKeys.buildRoomUsersDetailsKey(roomId.toString());
        long ttl= Optional.ofNullable(ttlInSeconds).orElse(3600L);

        Map<String, RoomUserDetails> mapOfRoomUserDetails = mapRoomUserDetailsById(users);

        try {
            redisTemplateRoomUserDetails.opsForHash().putAll(key, mapOfRoomUserDetails);
            redisTemplateRoomUserDetails.expire(key,Duration.ofSeconds(ttl));
        }catch (DataAccessException e){
            logger.error("Failed to upload Room Users details to Redis for rooID: {}", roomId, e);
            throw e;
        }

        logger.info("Uploaded {} users details to Redis for roomId: {} with TTL: {} seconds. Redis key: {}",
                users.size(), roomId, ttlInSeconds, key);
    }

    public Optional<RoomUserDetails> getRoomUserDetailsCache(@NotNull UUID roomId, UUID userId){
        String key= RedisKeys.buildRoomUsersDetailsKey(roomId.toString());
        RoomUserDetails obj;
        try {
            obj= (RoomUserDetails) redisTemplateRoomUserDetails.opsForHash().get(key,userId.toString());
        }catch (DataAccessException e){
            logger.error("Failed to get Room Users details to Redis for rooID: {}", roomId, e);
            throw e;
        }
        return Optional.ofNullable(obj);
    }

    private Map<String, RoomUserDetails> mapRoomUserDetailsById(@NotEmpty List<RoomUserDetails>users) {
        return users.stream()
                .collect(Collectors.toMap(RoomUserDetails::getUserId, Function.identity()));
    }

    private String formKey(List<String>segments){
        return String.join(":", segments);
    }
}
