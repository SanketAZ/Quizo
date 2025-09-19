package org.sxy.frontier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerEvaluation {
    private String questionId;
    private String submittedOptionId;
    private Boolean correct;
    private Integer obtainedMarks;
}
