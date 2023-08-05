package ru.practicum.shareit.booking.exception;

public class AddBookingByItemOwnerException extends RuntimeException {

    public AddBookingByItemOwnerException() {
        super("Пользователь не может бронировать свою вещь");
    }
}
