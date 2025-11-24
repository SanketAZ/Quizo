package org.sxy.frontier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDetailDTO {
        private UUID quizId;

        private UUID roomId;

        private UUID creatorUserId;

        private String title;

        private String description;

        private Integer questionCount;

        private Integer durationSec;

        private Instant startTime;

        private String status;
}
