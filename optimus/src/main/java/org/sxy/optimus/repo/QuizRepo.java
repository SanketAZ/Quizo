package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sxy.optimus.module.Quiz;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, UUID> {
    @Query("SELECT q.quizId FROM Quiz q WHERE q.quizId IN :ids")
    List<UUID> findExistingIds(@Param("ids") List<UUID> ids);
}
