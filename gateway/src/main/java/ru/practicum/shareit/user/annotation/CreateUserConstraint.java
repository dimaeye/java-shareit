package ru.practicum.shareit.user.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CreateUserValidator.class)
public @interface CreateUserConstraint {
    String message() default "Переданы некорректные данные для создания пользователя";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
