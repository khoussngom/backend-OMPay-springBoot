package com.khouss.UsersMicroservice.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CniValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidCni {
    String message() default "CNI invalide (doit correspondre au format Sénégalaise)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

