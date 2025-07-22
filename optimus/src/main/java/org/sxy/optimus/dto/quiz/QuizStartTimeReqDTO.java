package org.sxy.optimus.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import org.sxy.optimus.anotation.ValidInstantFormat;

public class QuizStartTimeReqDTO {

    @NotBlank(message = "Quiz id is required")
    private String quizId;

    @NotBlank(message = "start time is mandatory")
    @ValidInstantFormat
    private String startTime;

    @NotBlank(message = "user cannot be empty")
    private String creatorUserId;

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
}
