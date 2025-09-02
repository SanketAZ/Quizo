package org.sxy.optimus.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.sxy.optimus.event.QuizDetailCachedEvent;
import org.sxy.optimus.event.RoomUserDetailsCachedEvent;
import org.sxy.optimus.redis.RedisCacheRoomRepository;

import java.util.List;

@Component
public class RoomApplicationEventListener {

    private static final Logger log = LoggerFactory.getLogger(RoomApplicationEventListener.class);
    private final RedisCacheRoomRepository redisCacheRoomRepository;

    public RoomApplicationEventListener(RedisCacheRoomRepository redisCacheRoomRepository) {
        this.redisCacheRoomRepository = redisCacheRoomRepository;
    }

    @EventListener
    @Async("cacheWriterExecutor")
    public void onRoomUserDetailsCached(RoomUserDetailsCachedEvent evt) {
        try {
            redisCacheRoomRepository.cacheRoomUserDetails(List.of(evt.roomUserDetails()),evt.roomId(),evt.ttl().toSeconds());
        } catch (Exception e) {
            log.warn("Redis upload failed in listener: {}", e.getMessage(), e);
        }
    }
}
