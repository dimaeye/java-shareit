package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(
            BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public Booking addBooking(
            Booking booking, int bookerId, int itemId
    ) throws ItemNotFoundException, UserNotFoundException, AddBookingByItemOwnerException {
        User booker = getUser(bookerId);
        Item item = getItem(itemId);

        if (!item.getAvailable())
            throw new ItemNotAvailableException(itemId);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart()))
            throw new IllegalArgumentException(
                    "Время завершения бронирования должно быть после времени начала бронирования"
            );
        if (item.getOwner().getId() == bookerId)
            throw new AddBookingByItemOwnerException();

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
    @Transactional(readOnly = true)
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
    @Transactional
    public Booking approveBooking(
            int bookingId, int ownerId, boolean isApproved
    ) throws BookingNotFoundException, UserNotItemOwnerInBookingException, BadBookingStatusForApproveException {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getItem().getOwner().getId() != ownerId)
            throw new UserNotItemOwnerInBookingException(bookingId, ownerId);

        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new BadBookingStatusForApproveException(
                    "Статус бронирования " + bookingId + " уже изменен на " + booking.getStatus()
            );

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsOfUserByState(
            int bookerId, BookingState bookingState, int from, int size
    ) throws BookingNotFoundException {
        if (size <= 0)
            throw new IllegalArgumentException("Размер не должен быть меньше единицы.");

        List<Booking> allBookings;
        Pageable pageable = PageRequest.of(from / size, size);
        switch (bookingState) {
            case CURRENT:
                allBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case FUTURE:
                allBookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), pageable
                );
                break;
            case PAST:
                allBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case WAITING:
                allBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.WAITING, pageable
                );
                break;
            case REJECTED:
                allBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.REJECTED, pageable
                );
                break;
            default:
                allBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
        }

        if (allBookings.isEmpty())
            throw new BookingNotFoundException();
        else
            return allBookings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsOfUserItems(
            int ownerId, BookingState bookingState, int from, int size
    ) throws BookingNotFoundException {
        if (size <= 0)
            throw new IllegalArgumentException("Размер не должен быть меньше единицы.");

        List<Booking> allBookings;
        Pageable pageable = PageRequest.of(from / size, size);

        List<Integer> itemIds = getAllItemsByOwnerId(ownerId)
                .stream().map(Item::getId).collect(Collectors.toList());

        if (itemIds.isEmpty())
            throw new BookingNotFoundException();

        switch (bookingState) {
            case CURRENT:
                allBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                        itemIds, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case FUTURE:
                allBookings = bookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(
                        itemIds, LocalDateTime.now(), pageable
                );
                break;
            case PAST:
                allBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(
                        itemIds, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case WAITING:
                allBookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(
                        itemIds, BookingStatus.WAITING, pageable
                );
                break;
            case REJECTED:
                allBookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(
                        itemIds, BookingStatus.REJECTED, pageable
                );
                break;
            default:
                allBookings = bookingRepository.findAllByItemIdInOrderByStartDesc(itemIds, pageable);
        }

        if (allBookings.isEmpty())
            throw new BookingNotFoundException();
        else
            return allBookings;
    }

    private User getUser(int userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Item getItem(int itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private List<Item> getAllItemsByOwnerId(int ownerId) throws ItemNotFoundException {
        List<Item> items = itemRepository.findByOwnerId(ownerId, Pageable.unpaged());

        if (items.isEmpty())
            throw new ItemNotFoundException();
        else
            return items;
    }
}
