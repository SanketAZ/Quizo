package org.sxy.optimus.validation;

import java.util.List;

public class ValidationResult {
    private int index;
    private List<ValidationError> errors;

    public ValidationResult(int index, List<ValidationError> errors) {
        this.index = index;
        this.errors = errors;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}
