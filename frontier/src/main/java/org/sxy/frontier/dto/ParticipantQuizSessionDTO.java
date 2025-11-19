package org.sxy.frontier.dto;

import jakarta.persistence.*;
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
public class ParticipantQuizSessionDTO {
    private UUID sessionId;

    private UUID quizId;

    private UUID userId;

    private UUID roomId;

    private String status;

    private Instant startTime;

    private Instant endTime;

    private Instant finalEndTime;

    private Integer currentIndex;

    private String sequenceLabel;
}
