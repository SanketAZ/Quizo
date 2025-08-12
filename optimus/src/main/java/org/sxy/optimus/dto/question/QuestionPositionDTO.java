package org.sxy.optimus.dto.question;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class QuestionPositionDTO {
    @NotEmpty
    private String questionId;

    @NotNull
    @Min(1)
    private Integer position;

    public QuestionPositionDTO() {
    }

    public QuestionPositionDTO(String questionId, Integer position) {
        this.questionId = questionId;
        this.position = position;
    }
    public QuestionPositionDTO(UUID questionId, Integer position) {
        this.questionId = questionId.toString();
        this.position = position;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
