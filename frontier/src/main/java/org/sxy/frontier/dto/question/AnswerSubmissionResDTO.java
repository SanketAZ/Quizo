package org.sxy.frontier.dto.question;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerSubmissionResDTO {
    @NotBlank(message = "OptionId must be present")
    String submittedOptionId;
    @NotBlank(message = "Question Id must be present")
    String questionId;
    @NotBlank(message = "is correct field must be present")
    Boolean correct;
    @NotBlank(message = "Obtained marks must be present")
    Integer obtainedMarks;
}
