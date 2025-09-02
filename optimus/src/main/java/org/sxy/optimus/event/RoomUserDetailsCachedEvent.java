package org.sxy.optimus.event;

import org.sxy.optimus.dto.pojo.RoomUserDetails;

import java.time.Duration;
import java.util.UUID;

public record RoomUserDetailsCachedEvent(UUID roomId,RoomUserDetails roomUserDetails, Duration ttl) {
}
