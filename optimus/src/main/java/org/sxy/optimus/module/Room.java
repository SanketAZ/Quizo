package org.sxy.optimus.module;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    private String title;

    private String description;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false,nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",updatable = false,nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "room",fetch = FetchType.LAZY)
    private Set<RoomQuiz> roomQuizes;

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public Set<RoomQuiz> getRoomQuizes() {
        return roomQuizes;
    }

    public void setRoomQuizes(Set<RoomQuiz> roomQuizes) {
        this.roomQuizes = roomQuizes;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Room room)) return false;
        return Objects.equals(roomId, room.roomId) && Objects.equals(ownerUserId, room.ownerUserId) && Objects.equals(title, room.title) && Objects.equals(description, room.description) && Objects.equals(createdAt, room.createdAt) && Objects.equals(updatedAt, room.updatedAt) && Objects.equals(roomQuizes, room.roomQuizes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, ownerUserId, title, description, createdAt, updatedAt, roomQuizes);
    }
}