package ru.practicum.shareit.user.annotation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class CreateUserValidator implements ConstraintValidator<CreateUserConstraint, UserDTO> {
    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = userDTO.getEmail() != null && !userDTO.getEmail().isBlank();
        if (!result)
            log.error("Не передан email пользователя");
        return result;
    }
}
