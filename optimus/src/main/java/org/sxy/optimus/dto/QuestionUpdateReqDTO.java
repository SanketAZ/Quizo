package org.sxy.optimus.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class QuestionUpdateReqDTO {
    @NotBlank(message = "Question id cannot be empty")
    private String questionId;

    @NotBlank(message = "text cannot be empty")
    private String  text;

    @Min(1)
    private Integer weight;

    @Valid
    private List<OptionUpdateReqDTO> options;

    @Valid
    private List<OptionRequestDTO> newOptions;

    public QuestionUpdateReqDTO() {
    }

    public QuestionUpdateReqDTO(String questionId, Integer weight, String text) {
        this.questionId = questionId;
        this.weight = weight;
        this.text = text;
    }

    public List<OptionRequestDTO> getNewOptions() {
        return newOptions;
    }

    public void setNewOptions(List<OptionRequestDTO> newOptions) {
        this.newOptions = newOptions;
    }

    public List<OptionUpdateReqDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionUpdateReqDTO> options) {
        this.options = options;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
