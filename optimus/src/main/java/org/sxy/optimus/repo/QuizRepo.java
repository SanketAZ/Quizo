package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.module.Room;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, UUID>, JpaSpecificationExecutor<Quiz> {
    @Query("SELECT q.quizId FROM Quiz q WHERE q.quizId IN :ids")
    List<UUID> findExistingIds(@Param("ids") List<UUID> ids);

    boolean existsByQuizIdAndCreatorUserId(UUID quizId, UUID creatorUserId);

    @Query("""
    SELECT DISTINCT q
    FROM Quiz q
    JOIN FETCH q.questions que
    JOIN fetch que.options op
    WHERE q.quizId=:quizId
    """)
    Quiz getQuizWithAllQuestions(@Param("quizId") UUID quizId);

    @Query("SELECT q.quizId FROM Quiz q WHERE q.room.roomId = :roomId")
    List<UUID> findQuizIdsByRoomId(@Param("roomId") UUID roomId);

    @Query("SELECT COUNT(q) > 0 FROM Quiz q WHERE q.quizId = :quizId AND q.room.roomId = :roomId")
    boolean existsByQuizIdAndRoomId(@Param("quizId") UUID quizId, @Param("roomId") UUID roomId);
}
