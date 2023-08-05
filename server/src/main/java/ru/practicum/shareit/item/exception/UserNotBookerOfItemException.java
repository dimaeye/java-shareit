package ru.practicum.shareit.item.exception;

public class UserNotBookerOfItemException extends RuntimeException {

    public UserNotBookerOfItemException(String message) {
        super(message);
    }
}