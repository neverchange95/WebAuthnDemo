package de.thi.bachelorthesis.fido2.rpserver.general;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = Base64EncodedValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Encoded {
    String message() default "must be a well-formed base64";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}