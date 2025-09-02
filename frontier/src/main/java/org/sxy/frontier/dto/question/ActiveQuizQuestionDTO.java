package org.sxy.frontier.dto.question;

import jakarta.validation.constraints.NotEmpty;
import org.sxy.frontier.dto.option.ActiveQuizOptionDTO;
import org.sxy.frontier.redis.dto.OptionCacheDTO;

import java.util.List;

public class ActiveQuizQuestionDTO {
    @NotEmpty
    private String questionId;
    @NotEmpty
    private String  text;
    @NotEmpty
    private Integer weight;
    @NotEmpty
    List< @NotEmpty ActiveQuizOptionDTO> options;


    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public List<ActiveQuizOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<ActiveQuizOptionDTO> options) {
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
}
