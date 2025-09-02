package org.sxy.frontier.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;
import org.sxy.frontier.redis.dto.RoomUserDetailsCache;
import org.sxy.frontier.utility.RedisKeys;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomCacheRepo {

    private final RedisTemplate<String, RoomUserDetailsCache> rtRoomUserDetailsCache;
    private final HashOperations<String, String,RoomUserDetailsCache> hashOpsRoomUserDetailsCache;



    @Autowired
    public RoomCacheRepo(@Qualifier("String-RoomUserDetailsCache") RedisTemplate<String, RoomUserDetailsCache> rtRoomUserDetailsCache) {
        this.rtRoomUserDetailsCache = rtRoomUserDetailsCache;
        this.hashOpsRoomUserDetailsCache = rtRoomUserDetailsCache.opsForHash();
    }

    public boolean isRoomUsersLoaded(UUID roomId) {
        String key= RedisKeys.buildRoomUsersDetailsKey(roomId.toString());
        long size=hashOpsRoomUserDetailsCache.size(key);
        return size>0;
    }

    public boolean isRoomUserPresent(UUID roomId,UUID userId) {
        String key= RedisKeys.buildRoomUsersDetailsKey(roomId.toString());
        return hashOpsRoomUserDetailsCache.hasKey(key,userId.toString());
    }
}