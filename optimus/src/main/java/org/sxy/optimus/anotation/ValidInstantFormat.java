package org.sxy.optimus.anotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.sxy.optimus.dto.validators.InstantFormatValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InstantFormatValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInstantFormat {
    String message() default "Invalid Instant format. Expected ISO-8601 (e.g. 2025-07-18T13:00:00Z)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowBlank() default false;
}
