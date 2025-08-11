package org.sxy.optimus.dto.question;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class QuestionDeleteReqDTO {
    @NotEmpty
    List<String> questionIds;

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }
}
