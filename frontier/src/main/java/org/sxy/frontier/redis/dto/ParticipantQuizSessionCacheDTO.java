package org.sxy.frontier.redis.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantQuizSessionCacheDTO {
    private String sessionId;
    private String quizId;
    private String userId;
    private String roomId;
    private String status;

    private Long startTime;
    private Long endTime;
    private Long finalEndTime;

    private Integer currentIndex;
    private String sequenceLabel;
}
