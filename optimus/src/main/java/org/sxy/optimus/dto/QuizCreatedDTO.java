package org.sxy.optimus.dto;

public class QuizCreatedDTO {
    private String quizId;
    private String creatorUserId;
    private String title;

    public QuizCreatedDTO(String quizId, String creatorUserId, String title) {
        this.quizId = quizId;
        this.creatorUserId = creatorUserId;
        this.title = title;
    }

    public QuizCreatedDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
}
