package org.sxy.optimus.dto.option;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class OptionRequestDTO {
    private String optionId;

    @NotBlank(message = "text cannot be empty")
    private String text;

    @NotNull(message = "isCorrect filed cannot be empty")
    private Boolean isCorrect;

    public OptionRequestDTO() {
    }

    public OptionRequestDTO(String optionId, Boolean isCorrect, String text) {
        this.optionId = optionId;
        this.isCorrect = isCorrect;
        this.text = text;
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

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionRequestDTO that)) return false;
        return Objects.equals(optionId, that.optionId) && Objects.equals(text, that.text) && Objects.equals(isCorrect, that.isCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionId, text, isCorrect);
    }
}
