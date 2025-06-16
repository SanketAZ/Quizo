package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sxy.optimus.module.Quiz;

import java.util.UUID;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, UUID> {

}
