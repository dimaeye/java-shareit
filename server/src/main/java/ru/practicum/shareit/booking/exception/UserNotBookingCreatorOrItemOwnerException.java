package ru.practicum.shareit.booking.exception;

public class UserNotBookingCreatorOrItemOwnerException extends RuntimeException {

    public UserNotBookingCreatorOrItemOwnerException(int bookingId, int userId) {
        super("Пользователь id= " + userId + " не является автором бронирования id = " + bookingId
                + " , либо владельцем вещи!");
    }

}
