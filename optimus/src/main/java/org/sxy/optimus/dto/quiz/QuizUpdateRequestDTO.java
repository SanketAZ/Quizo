package org.sxy.optimus.dto.quiz;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class QuizUpdateRequestDTO {
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

    public QuizUpdateRequestDTO() {
    }

    public QuizUpdateRequestDTO(String creatorUserId, String title, String description, Integer questionCount, Integer durationSec, String startTime) {
        this.creatorUserId = creatorUserId;
        this.title = title;
        this.description = description;
        this.questionCount = questionCount;
        this.durationSec = durationSec;
        this.startTime = startTime;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(Integer durationSec) {
        this.durationSec = durationSec;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
