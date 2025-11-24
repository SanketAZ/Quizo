package org.sxy.optimus.redis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizDetailCacheDTO {

        private String quizId;

        private String roomId;

        private String creatorUserId;

        private String title;

        private String description;

        private Integer questionCount;

        private Integer durationSec;

        private Long startTime;

        private String status;
}
