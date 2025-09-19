package org.sxy.frontier.redis.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sxy.frontier.redis.dto.OptionCacheDTO;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCacheDTO {
    @NotEmpty
    private String questionId;

    @NotEmpty
    private String  text;

    @NotEmpty
    private Integer weight;

    @NotEmpty
    List< @NotEmpty OptionCacheDTO> options;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public List<OptionCacheDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionCacheDTO> options) {
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
