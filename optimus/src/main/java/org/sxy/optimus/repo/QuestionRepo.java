package org.sxy.optimus.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sxy.optimus.module.Question;
import org.sxy.optimus.projection.QuestionWithOptionsProjection;

import java.util.UUID;

public interface QuestionRepo extends JpaRepository<Question, UUID> {
    Question findQuestionByQuestionId(UUID questionId);

    @Query("SELECT DISTINCT q FROM Question q JOIN FETCH q.options WHERE q.quiz.quizId=:quizId")
    Page<QuestionWithOptionsProjection> findQuestionWithOptionsByQuizId(@Param("quizId") UUID quizId, Pageable pageable);

}
