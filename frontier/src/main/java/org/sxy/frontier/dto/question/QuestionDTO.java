package org.sxy.frontier.dto.question;


import org.sxy.frontier.dto.option.OptionDTO;

import java.util.List;

public class QuestionDTO {
    private String questionId;
    private String  text;
    private Integer weight;
    List<OptionDTO> options;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }
}
