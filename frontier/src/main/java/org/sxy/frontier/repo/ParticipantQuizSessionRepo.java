package org.sxy.frontier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sxy.frontier.module.ParticipantQuizSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantQuizSessionRepo extends JpaRepository<ParticipantQuizSession, UUID> {

    Optional<ParticipantQuizSession> findByRoomIdAndQuizIdAndUserId(UUID roomId, UUID quizId, UUID userId);
}
