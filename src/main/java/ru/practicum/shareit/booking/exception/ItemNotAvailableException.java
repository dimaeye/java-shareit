package ru.practicum.shareit.booking.exception;

public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException(int itemId) {
        super("Бронирование не может быть создано, т.к. вещь id= " + itemId + "недоступна!");
    }
}