package ru.practicum.shareit.item.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AddItemValidator.class)
public @interface AddItemConstraint {
    String message() default "Переданы некорректные данные для создания предмета";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
