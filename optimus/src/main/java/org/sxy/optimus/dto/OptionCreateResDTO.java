package org.sxy.optimus.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class OptionCreateResDTO {
    private String optionId;

    private String text;

    private Boolean isCorrect;

    public OptionCreateResDTO() {
    }

    public OptionCreateResDTO(String optionId, Boolean isCorrect, String text) {
        this.optionId = optionId;
        this.isCorrect = isCorrect;
        this.text = text;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
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
        if (!(o instanceof OptionCreateResDTO that)) return false;
        return Objects.equals(optionId, that.optionId) && Objects.equals(text, that.text) && Objects.equals(isCorrect, that.isCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionId, text, isCorrect);
    }
}
