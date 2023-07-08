package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.UserNotBookingCreatorOrItemOwnerException;
import ru.practicum.shareit.booking.exception.UserNotItemOwnerInBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface BookingService {
    Booking addBooking(Booking booking, int ownerId, int itemId) throws ItemNotFoundException, UserNotFoundException;

    Booking getBooking(
            int bookingId, int ownerId
    ) throws BookingNotFoundException, UserNotBookingCreatorOrItemOwnerException;

    Booking approveBooking(
            int bookingId, int ownerId, boolean isApproved
    ) throws BookingNotFoundException, UserNotItemOwnerInBookingException;

    List<Booking> getAllBookingsOfUserByState(int ownerId, BookingState bookingState) throws BookingNotFoundException;

    List<Booking> getAllBookingsOfUserItems(int ownerId, BookingState bookingState) throws BookingNotFoundException;
}
