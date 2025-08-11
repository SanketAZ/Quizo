package org.sxy.optimus.dto.question;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public class QuestionDeleteResDTO {
    @NotEmpty
    List<String> questionIds;

    int deletedCount;

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }

    public int getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(int deletedCount) {
        this.deletedCount = deletedCount;
    }
}
