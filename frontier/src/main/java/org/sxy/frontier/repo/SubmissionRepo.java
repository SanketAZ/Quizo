package org.sxy.frontier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sxy.frontier.module.Submission;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepo extends JpaRepository<Submission, UUID> {
    Optional<Submission> findByUserIdAndQuizIdAndRoomIdAndQuestionId(
            UUID userId,
            UUID quizId,
            UUID roomId,
            UUID questionId
    );
    boolean existsByUserIdAndQuizIdAndRoomIdAndQuestionId(UUID userId, UUID quizId, UUID roomId, UUID questionId);
}