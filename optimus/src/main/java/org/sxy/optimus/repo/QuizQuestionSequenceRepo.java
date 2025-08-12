package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sxy.optimus.dto.question.QuestionPositionDTO;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.module.QuizQuestionSequence;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizQuestionSequenceRepo extends JpaRepository<QuizQuestionSequence, UUID> {

    @Query("SELECT DISTINCT qs FROM QuizQuestionSequence qs WHERE qs.quiz.quizId=:quizId")
    List<QuizQuestionSequence> findByQuizId(@Param("quizId") UUID quizId);

    @Modifying
    @Query(value = "SET CONSTRAINTS uq_pos, uq_question DEFERRED", nativeQuery = true)
    void deferConstraints();

    List<QuizQuestionSequence> findAllByQuizOrderByPositionAsc(Quiz quiz);


    @Query("""
            SELECT DISTINCT new org.sxy.optimus.dto.question.QuestionPositionDTO(qs.question.questionId,qs.position) 
            FROM QuizQuestionSequence qs 
            WHERE qs.quiz.quizId=:quizId 
            ORDER BY qs.position ASC
                        """)
    List<QuestionPositionDTO> findAllQuestionPositionsByQuiz(UUID quizId);


}