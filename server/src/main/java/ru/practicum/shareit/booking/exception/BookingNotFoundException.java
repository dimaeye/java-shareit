package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(int bookingId) {
        super("Бронирование с id=" + bookingId + " не найдено!");
    }

    public BookingNotFoundException() {
        super("Бронирование не найдено!");
    }
}
