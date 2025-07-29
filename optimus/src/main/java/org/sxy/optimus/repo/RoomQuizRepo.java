package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sxy.optimus.module.RoomQuiz;
import org.sxy.optimus.module.compKey.RoomQuizId;

@Repository
public interface RoomQuizRepo extends JpaRepository<RoomQuiz, RoomQuizId> {

}