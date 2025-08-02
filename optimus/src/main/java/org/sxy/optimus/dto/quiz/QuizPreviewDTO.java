package org.sxy.optimus.dto.quiz;

import java.util.List;

public class QuizPreviewDTO {
    private String quizId;
    private List<String> questionIds;

    public QuizPreviewDTO() {
    }

    public QuizPreviewDTO(String quizId, List<String> questionIds) {
        this.quizId = quizId;
        this.questionIds = questionIds;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }
}
