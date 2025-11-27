package org.sxy.frontier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionDTO {
    UUID submissionId;
    UUID userId;
    UUID questionId;
    UUID quizId;
    UUID roomId;
    String selectedOptionId;
    Boolean isCorrect;
    Integer obtainedMarks;
}
