package org.sxy.optimus.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.sxy.optimus.enums.QuizStatus;
import org.sxy.optimus.module.Quiz;
import org.sxy.optimus.module.Room;
import org.sxy.optimus.module.RoomQuiz;
import org.sxy.optimus.module.RoomUser;

import java.util.UUID;

public class QuizSpecifications {
    public static Specification<Quiz> hasStatus(QuizStatus status) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"),status);
    }

    public static Specification<Quiz> hasStatus(String status) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"),status);
    }

    public static Specification<Quiz> hasRoomId(UUID roomId) {
        return (root, criteriaQuery,criteriaBuilder) ->{
            criteriaQuery.distinct(true);
            Join<Quiz, RoomQuiz> roomQuizJoin = root.join("roomQuizzes", JoinType.INNER);
            return criteriaBuilder.equal(roomQuizJoin.get("id").get("roomId"),roomId);
        };
    }

    public static Specification<Quiz> hasOwnerId(UUID ownerId) {
        return (root, criteriaQuery,criteriaBuilder) ->
                criteriaBuilder.equal(root.get("creatorUserId"),ownerId);
    }

    public static Specification<Quiz> userIsRoomParticipant(UUID userId) {
        return (root, criteriaQuery,criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<Quiz,RoomQuiz> roomQuizJoin = root.join("roomQuizzes", JoinType.INNER);
            Join<RoomQuiz, Room> roomJoin = roomQuizJoin.join("room", JoinType.INNER);
            Join<Room, RoomUser> roomUserJoin = roomJoin.join("roomUsers", JoinType.INNER);

            return criteriaBuilder.equal(roomUserJoin.get("roomUserId").get("userId"), userId);
        };
    }

    public static Specification<Quiz> notOwnedBy(UUID userId) {
        return (root, query, cb) -> cb.notEqual(root.get("creatorUserId"), userId);
    }
}
