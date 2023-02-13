package com.digitalmoneyhouse.iamservice.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "\\(?\\d{2}\\)?[ \\-]?\\d{5}[ \\-]?\\d{4}", message = "invalid Brazilian mobile number")
public @interface BrazilianCellPhone {
    String message() default "invalid Brazilian mobile number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
