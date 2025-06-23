package org.sxy.optimus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OptionUpdateReqDTO {
    @NotBlank(message = "OptionId is required")
    private String optionId;

    @NotBlank(message = "text cannot be empty")
    private String text;

    @NotNull(message = "isCorrect filed cannot be empty")
    private Boolean isCorrect;

    public OptionUpdateReqDTO() {
    }

    public OptionUpdateReqDTO(String optionId, Boolean isCorrect, String text) {
        this.optionId = optionId;
        this.isCorrect = isCorrect;
        this.text = text;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
