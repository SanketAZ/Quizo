package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sxy.optimus.module.QuizQuestionSequence;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionSequenceRepo extends JpaRepository<QuizQuestionSequence, UUID> {
    List<QuizQuestionSequence> findByQuizId(UUID quizId);

    @Modifying
    @Query(value = "SET CONSTRAINTS uq_pos, uq_question DEFERRED", nativeQuery = true)
    void deferConstraints();


}