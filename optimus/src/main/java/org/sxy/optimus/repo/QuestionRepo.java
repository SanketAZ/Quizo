package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sxy.optimus.module.Question;

import java.util.UUID;

public interface QuestionRepo extends JpaRepository<Question, UUID> {
    Question findQuestionByQuestionId(UUID questionId);
}
