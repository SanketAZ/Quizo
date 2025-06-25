package org.sxy.optimus.dto.question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.sxy.optimus.dto.option.OptionRequestDTO;

import java.util.List;
import java.util.Objects;

public class QuestionRequestDTO {
    private String questionId;

    @NotBlank(message = "text cannot be empty")
    private String  text;

    @Min(1)
    private Integer weight;

    @Valid
    private List<OptionRequestDTO> options;

    public QuestionRequestDTO() {
    }

    public QuestionRequestDTO(Integer weight, String text, String questionId) {
        this.weight = weight;
        this.text = text;
        this.questionId = questionId;
    }

    public List<OptionRequestDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionRequestDTO> options) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QuestionRequestDTO that)) return false;
        return Objects.equals(questionId, that.questionId) && Objects.equals(text, that.text) && Objects.equals(weight, that.weight) && Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, text, weight, options);
    }
}
