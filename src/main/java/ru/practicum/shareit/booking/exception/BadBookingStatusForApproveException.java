package ru.practicum.shareit.booking.exception;

public class BadBookingStatusForApproveException extends RuntimeException {

    public BadBookingStatusForApproveException(String message) {
        super(message);
    }
}
