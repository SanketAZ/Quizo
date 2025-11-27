package org.sxy.optimus.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sxy.optimus.dto.quiz.QuizDisplayDTO;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.module.Room;

import java.util.List;
import java.util.UUID;

public interface RoomRepo extends JpaRepository<Room, UUID> {
    boolean existsByRoomIdAndOwnerUserId(UUID roomId, UUID ownerUserId);
    Page<Room> findByOwnerUserId(UUID ownerUserId, Pageable pageable);

    //For joined rooms (user is participant in RoomUser table)-
    @Query("""
        SELECT DISTINCT r FROM Room r
        JOIN RoomUser ru ON ru.roomUserId.roomId = r.roomId
        WHERE ru.roomUserId.userId = :userId
    """)
    Page<Room> findRoomsWhereUserIsParticipant(@Param("userId") UUID userId, Pageable pageable);

}
