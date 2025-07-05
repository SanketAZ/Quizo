package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sxy.optimus.module.RoomUser;
import org.sxy.optimus.module.compKey.RoomUserId;
import org.sxy.optimus.projection.RoomUserIdProjection;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public interface RoomUserRepo extends JpaRepository<RoomUser, UUID> {

    @Query("SELECT ru.roomUserId FROM RoomUser ru WHERE ru.roomUserId IN :ids")
    List<RoomUserId> findExistingRoomUsers(@Param("ids") List<RoomUserId> ids);

}
