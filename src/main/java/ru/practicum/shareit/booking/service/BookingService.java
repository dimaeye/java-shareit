package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface BookingService {
    Booking addBooking(
            Booking booking, int ownerId, int itemId
    ) throws ItemNotFoundException, UserNotFoundException, AddBookingByItemOwnerException;

    Booking getBooking(
            int bookingId, int ownerId
    ) throws BookingNotFoundException, UserNotBookingCreatorOrItemOwnerException;

    Booking approveBooking(
            int bookingId, int ownerId, boolean isApproved
    ) throws BookingNotFoundException, UserNotItemOwnerInBookingException, BadBookingStatusForApproveException;

    List<Booking> getAllBookingsOfUserByState(int ownerId, BookingState bookingState, int from, int size) throws BookingNotFoundException;

    List<Booking> getAllBookingsOfUserItems(int ownerId, BookingState bookingState, int from, int size) throws BookingNotFoundException;
}
