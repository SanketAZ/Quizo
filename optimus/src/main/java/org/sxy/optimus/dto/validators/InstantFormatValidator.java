package org.sxy.optimus.dto.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.sxy.optimus.anotation.ValidInstantFormat;

import java.time.Instant;

public class InstantFormatValidator implements ConstraintValidator<ValidInstantFormat, Object> {

    private boolean allowBlank;

    @Override
    public void initialize(ValidInstantFormat constraintAnnotation) {
        this.allowBlank = constraintAnnotation.allowBlank();
    }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null){
            return allowBlank;
        }

        if(value instanceof String str){
            try {
                Instant.parse(str);
                return true;
            }catch(Exception e){
                return false;
            }
        }
        return (value instanceof Instant);
    }
}
