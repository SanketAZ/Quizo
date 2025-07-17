package org.sxy.optimus.dto.option;

import jakarta.validation.constraints.NotEmpty;

public class OptionCacheDTO {
    @NotEmpty
    private String optionId;
    @NotEmpty
    private String text;
    @NotEmpty
    private Boolean isCorrect;

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

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
