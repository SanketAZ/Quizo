package org.sxy.optimus.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.repo.RoomRepo;

import java.util.UUID;

@Service
public class AccessControlService {

    private final RoomRepo roomRepo;

    @Autowired
    public AccessControlService(RoomRepo roomRepo) {
        this.roomRepo = roomRepo;
    }


    public void validateRoomAccess(@Nonnull UUID userId, @Nullable UUID roomId) {
        if (roomId == null) return;

        if (!roomRepo.existsById(roomId)) {
            throw new ResourceDoesNotExitsException("Room", "roomId", roomId.toString());
        }
        if (!roomRepo.existsByRoomIdAndOwnerUserId(roomId, userId)) {
            throw new UnauthorizedActionException(
                    "User %s is not authorized to access room %s".formatted(userId, roomId)
            );
        }
    }
}
