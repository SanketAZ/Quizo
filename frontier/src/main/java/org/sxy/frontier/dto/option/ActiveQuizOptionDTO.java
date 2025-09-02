package org.sxy.frontier.dto.option;

import jakarta.validation.constraints.NotEmpty;

public class ActiveQuizOptionDTO {
    @NotEmpty
    private String optionId;
    @NotEmpty
    private String text;

    public ActiveQuizOptionDTO() {
    }

    public ActiveQuizOptionDTO(String optionId, String text) {
        this.optionId = optionId;
        this.text = text;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
