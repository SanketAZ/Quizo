package org.sxy.frontier.dto.question;

import jakarta.validation.constraints.NotBlank;

public class AnswerSubmissionReqDTO {
    @NotBlank(message = "Question Id must be present")
    String questionId;
    @NotBlank(message = "OptionId must be present")
    String optionId;
    @NotBlank(message = "Submitted time stamp must be present")
    String submittedAt;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }
}
