package org.sxy.optimus.dto.quiz;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class QuizCreateDTO {
    @NotBlank(message = "user cannot be empty")
    private String creatorUserId;

    @NotBlank(message = "title cannot be empty")
    private String title;

    @NotBlank(message = "description cannot be empty")
    private String description;

    @Min(0)
    @Max(30)
    private Integer questionCount;

    @Min(900)
    @Max(3600)
    private Integer durationSec;

    @NotBlank(message = "start time is mandatory")
    private String startTime;

    public QuizCreateDTO() {
    }

    public QuizCreateDTO(String creatorUserId, String startTime, Integer durationSec, Integer questionCount, String description, String title) {
        this.creatorUserId = creatorUserId;
        this.startTime = startTime;
        this.durationSec = durationSec;
        this.questionCount = questionCount;
        this.description = description;
        this.title = title;
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
}