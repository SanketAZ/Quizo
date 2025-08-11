package org.sxy.optimus.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sxy.optimus.module.Question;
import org.sxy.optimus.projection.QuestionWithOptionsProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepo extends JpaRepository<Question, UUID> {

    @Query("SELECT q FROM Question q JOIN FETCH q.quiz WHERE q.questionId=:questionId")
    Optional<Question> findQuestionByQuestionIdWithQuiz(UUID questionId);

    @Query("SELECT DISTINCT q FROM Question q JOIN FETCH q.options WHERE q.quiz.quizId=:quizId")
    Page<QuestionWithOptionsProjection> findQuestionWithOptionsByQuizId(@Param("quizId") UUID quizId, Pageable pageable);

    //Total number of questions for give quiz
    @Query("SELECT count (*) FROM Question q WHERE q.quiz.quizId=:quizId")
    Integer countByQuizId(@Param("quizId")UUID quizId);

    //All questions id for give quiz
    @Query("SELECT q.questionId FROM Question q WHERE q.quiz.quizId=:quizId")
    List<UUID> findQuestionIdsByQuizId(@Param("quizId")UUID quizId);

    //Delete the given questions for quiz
    @Modifying
    @Query("DELETE FROM Question q WHERE q.questionId IN :ids AND q.quiz.quizId= :quizId")
    void deleteByIdsAndQuizId(@Param("ids") List<UUID> ids,@Param("quizId") UUID quizId);

    @Query("SELECT q.questionId FROM Question q WHERE q.quiz.quizId=:quizId")
    List<UUID> findQuestionIdsForQuiz(@Param("quizId") UUID quizId);
}
