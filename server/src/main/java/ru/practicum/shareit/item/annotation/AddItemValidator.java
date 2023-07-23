package ru.practicum.shareit.item.annotation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class AddItemValidator implements ConstraintValidator<AddItemConstraint, ItemDTO> {
    @Override
    public boolean isValid(ItemDTO itemDTO, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = true;

        if (itemDTO.getName() == null || itemDTO.getName().isBlank()) {
            log.error("Название предмета не может отсутствовать");
            result = false;
        }
        if (itemDTO.getDescription() == null || itemDTO.getDescription().isBlank()) {
            log.error("Описание предмета не может отсутствовать");
            result = false;
        }
        if (itemDTO.getAvailable() == null) {
            log.error("Не передан статус доступности предмета");
            result = false;
        }

        return result;
    }
}
