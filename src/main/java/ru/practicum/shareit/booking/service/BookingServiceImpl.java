package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }


    @Override
    public Booking addBooking(
            Booking booking, int bookerId, int itemId
    ) throws ItemNotFoundException, UserNotFoundException {
        User booker = userService.getUser(bookerId);
        Item item = itemService.getItem(itemId);

        if (!item.getAvailable())
            throw new ItemNotAvailableException(itemId);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart()))
            throw new IllegalArgumentException(
                    "Время завершения бронирования должно быть после времени начала бронирования"
            );
        if (item.getOwner().getId() == bookerId)
            throw new IllegalArgumentException("Пользователь не может бронировать свою вещь");

        List<Booking> alreadyCreatedBookings = bookingRepository
                .findAllByItemIdAndStartBetweenAndEndBetween(item.getId(), booking.getStart(), booking.getEnd());
        if (!alreadyCreatedBookings.isEmpty())
            throw new AlreadyReservedItemException(itemId);

        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(
            int bookingId, int userId
    ) throws BookingNotFoundException, UserNotBookingCreatorOrItemOwnerException {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId)
            throw new UserNotBookingCreatorOrItemOwnerException(bookingId, userId);

        return booking;
    }

    @Override
    public Booking approveBooking(int bookingId, int ownerId, boolean isApproved) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getItem().getOwner().getId() != ownerId)
            throw new UserNotItemOwnerInBookingException(bookingId, ownerId);

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookingsOfUserByState(
            int bookerId, BookingState bookingState
    ) throws BookingNotFoundException {
        List<Booking> allBookings;
        switch (bookingState) {
            case CURRENT:
                allBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now()
                );
            case FUTURE:
                allBookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case PAST:
                allBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now()
                );
            case WAITING:
                allBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED:
                allBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            default:
                allBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
        }

        if (allBookings.isEmpty())
            throw new BookingNotFoundException();
        else
            return allBookings;
    }

    @Override
    public List<Booking> getAllBookingsOfUserItems(
            int ownerId, BookingState bookingState
    ) throws BookingNotFoundException {
        List<Booking> allBookings;

        List<Integer> itemIds = itemService.getAllItemsByOwnerId(ownerId)
                .stream().map(Item::getId).collect(Collectors.toList());

        if (itemIds.isEmpty())
            throw new BookingNotFoundException();

        switch (bookingState) {
            case CURRENT:
                allBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                        itemIds, LocalDateTime.now(), LocalDateTime.now()
                );
            case FUTURE:
                allBookings = bookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(itemIds, LocalDateTime.now());
            case PAST:
                allBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(
                        itemIds, LocalDateTime.now(), LocalDateTime.now()
                );
            case WAITING:
                allBookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.WAITING);
            case REJECTED:
                allBookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.REJECTED);
            default:
                allBookings = bookingRepository.findAllByItemIdInOrderByStartDesc(itemIds);
        }

        if (allBookings.isEmpty())
            throw new BookingNotFoundException();
        else
            return allBookings;
    }
}
