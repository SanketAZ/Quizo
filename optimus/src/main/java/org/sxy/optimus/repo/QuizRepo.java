package org.sxy.optimus.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sxy.optimus.dto.quiz.QuizDisplayDTO;
import org.sxy.optimus.enums.QuizStatus;
import org.sxy.optimus.module.Quiz;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, UUID>, JpaSpecificationExecutor<Quiz> {
    @Query("SELECT q.quizId FROM Quiz q WHERE q.quizId IN :ids")
    List<UUID> findExistingIds(@Param("ids") List<UUID> ids);

    @Query("""
    SELECT q
    FROM RoomQuiz rq
    JOIN rq.quiz q
    WHERE rq.room.roomId =:roomId AND q.status=:status
    """)
    Page<Quiz> getQuizDisplayDTOByRoomId(@Param("roomId") UUID roomId, @Param("status") String status , Pageable pageable);

    boolean existsByQuizIdAndCreatorUserId(UUID quizId, UUID creatorUserId);


    @Query("""
    SELECT DISTINCT q
    FROM Quiz q
    JOIN FETCH q.questions que
    JOIN fetch que.options op
    WHERE q.quizId=:quizId
    """)
    Quiz getQuizWithAllQuestions(@Param("quizId") UUID quizId);

}
