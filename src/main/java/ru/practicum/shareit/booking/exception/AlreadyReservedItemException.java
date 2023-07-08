package ru.practicum.shareit.booking.exception;

public class AlreadyReservedItemException extends RuntimeException {

    public AlreadyReservedItemException(int itemId) {
        super("Бронирование не может быть создано, т.к. вещь id= " + itemId + " забронирована!");
    }
}
