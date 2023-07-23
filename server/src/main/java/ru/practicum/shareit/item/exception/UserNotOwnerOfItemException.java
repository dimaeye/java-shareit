package ru.practicum.shareit.item.exception;

public class UserNotOwnerOfItemException extends RuntimeException {
    public UserNotOwnerOfItemException(String message) {
        super(message);
    }
}
