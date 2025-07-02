package org.sxy.optimus.dto.quiz;

import java.time.Instant;
import java.util.UUID;

public class QuizDisplayDTO {

    private String quizId;

    private String creatorUserId;

    private String title;

    private String description;

    private Integer questionCount;

    private Integer durationSec;

    private String startTime;

    private String status;

    public QuizDisplayDTO() {
    }

    public QuizDisplayDTO(String quizId, String startTime, Integer durationSec, Integer questionCount, String description, String title, String creatorUserId, String status) {
        this.quizId = quizId;
        this.startTime = startTime;
        this.durationSec = durationSec;
        this.questionCount = questionCount;
        this.description = description;
        this.title = title;
        this.creatorUserId = creatorUserId;
        this.status = status;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(Integer durationSec) {
        this.durationSec = durationSec;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
