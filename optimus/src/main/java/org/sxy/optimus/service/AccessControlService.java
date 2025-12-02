package org.sxy.optimus.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sxy.optimus.exception.ResourceDoesNotExitsException;
import org.sxy.optimus.exception.UnauthorizedActionException;
import org.sxy.optimus.module.compKey.RoomUserId;
import org.sxy.optimus.repo.QuizRepo;
import org.sxy.optimus.repo.RoomRepo;
import org.sxy.optimus.repo.RoomUserRepo;

import java.util.UUID;

@Service
public class AccessControlService {

    private final RoomRepo roomRepo;
    private final RoomUserRepo roomUserRepo;
    private final QuizRepo quizRepo;

    @Autowired
    public AccessControlService(RoomRepo roomRepo, RoomUserRepo roomUserRepo, QuizRepo quizRepo) {
        this.roomRepo = roomRepo;
        this.roomUserRepo = roomUserRepo;
        this.quizRepo = quizRepo;
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

    public void validateQuizAccess(@Nonnull UUID userId, @Nullable UUID quizId) {
        if (quizId == null) return;

        if (!quizRepo.existsById(quizId)) {
            throw new ResourceDoesNotExitsException("Quiz", "quiz", quizId.toString());
        }
        if (!quizRepo.existsByQuizIdAndCreatorUserId(quizId, userId)) {
            throw new UnauthorizedActionException(
                    "User %s is not authorized to access quiz %s".formatted(userId, quizId)
            );
        }
    }

    public void validateRoomMembership(@Nonnull UUID userId, @Nonnull UUID roomId) {
        if (!roomRepo.existsById(roomId)) {
            throw new ResourceDoesNotExitsException("Room", "roomId", roomId.toString());
        }

        RoomUserId roomUserId = new RoomUserId(roomId, userId);
        if (!roomUserRepo.existsById(roomUserId)) {
            throw new UnauthorizedActionException(
                    "User %s is not a member of room %s".formatted(userId, roomId)
            );
        }
    }
}
