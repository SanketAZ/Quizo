package org.sxy.optimus.dto.question;

import org.sxy.optimus.dto.option.OptionCreateResDTO;

import java.util.List;
import java.util.Objects;

public class QuestionCreateResDTO {
    private String questionId;

    private String  text;

    private Integer weight;

    private List<OptionCreateResDTO> options;

    public QuestionCreateResDTO() {
    }

    public QuestionCreateResDTO(String questionId, Integer weight, String text) {
        this.questionId = questionId;
        this.weight = weight;
        this.text = text;
    }

    public List<OptionCreateResDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionCreateResDTO> options) {
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
        if (!(o instanceof QuestionCreateResDTO that)) return false;
        return Objects.equals(questionId, that.questionId) && Objects.equals(text, that.text) && Objects.equals(weight, that.weight) && Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, text, weight, options);
    }
}