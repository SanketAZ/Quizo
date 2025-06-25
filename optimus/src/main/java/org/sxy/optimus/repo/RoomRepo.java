package org.sxy.optimus.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sxy.optimus.module.Room;

import java.util.UUID;

public interface RoomRepo extends JpaRepository<Room, UUID> {
}
