package ru.practicum.shareit.booking.exception;

public class UserNotItemOwnerInBookingException extends RuntimeException {
    public UserNotItemOwnerInBookingException(int bookingId, int userId) {
        super("Пользователь id= " + userId + " не является владельцем вещи для бронирования с id= " + bookingId);
    }
}
