package org.sxy.frontier.dto.question;

import jakarta.validation.constraints.NotBlank;

public class AnswerSubmissionResDTO {
    @NotBlank(message = "OptionId must be present")
    String optionId;
    @NotBlank(message = "Question Id must be present")
    String questionId;
    @NotBlank(message = "is correct field must be present")
    Boolean correct;
    @NotBlank(message = "Obtained marks must be present")
    Integer obtainedMarks;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(Integer obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }
}
