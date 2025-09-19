package org.sxy.frontier.redis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionCacheDTO {
    String userId;
    String questionId;
    String quizId;
    String roomId;
    String selectedOptionId;
    boolean isCorrect;
    int obtainedMarks;
}
