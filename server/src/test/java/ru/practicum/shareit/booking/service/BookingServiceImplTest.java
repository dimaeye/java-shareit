package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.exception.BadBookingStatusForApproveException;
import ru.practicum.shareit.booking.exception.UserNotBookingCreatorOrItemOwnerException;
import ru.practicum.shareit.booking.exception.UserNotItemOwnerInBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private final BookingRepository mockBookingRepository = mock(BookingRepository.class);
    private final ItemRepository mockItemRepository = mock(ItemRepository.class);
    private final UserRepository mockUserRepository = mock(UserRepository.class);

    private final BookingService bookingService = new BookingServiceImpl(
            mockBookingRepository, mockItemRepository, mockUserRepository
    );

    private final EasyRandom generator = new EasyRandom();

    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
    }

    @Test
    void addBooking() {
        when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));

        Item item = generator.nextObject(Item.class);
        when(mockItemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        when(mockBookingRepository.findAllByItemIdAndStartBetweenAndEndBetween(
                anyInt(), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(Collections.emptyList());

        Booking booking = generator.nextObject(Booking.class);
        when(mockBookingRepository.save(any())).thenReturn(booking);

        Booking addedBooking = bookingService.addBooking(booking, user.getId(), item.getId());

        assertEquals(booking, addedBooking);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenAddBookingWithBadUserId() {
        User user = generator.nextObject(User.class);
        when(mockUserRepository.findById(user.getId() + 1))
                .thenReturn(Optional.of(generator.nextObject(User.class)));

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.addBooking(generator.nextObject(Booking.class), user.getId(), generator.nextInt())
        );

        assertAll(
                () -> assertEquals(
                        new UserNotFoundException(user.getId()).getMessage(),
                        userNotFoundException.getMessage()
                ),
                () -> verify(mockItemRepository, times(0)).findById(anyInt()),
                () -> verify(mockBookingRepository, times(0))
                        .findAllByItemIdAndStartBetweenAndEndBetween(
                                anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)
                        ),
                () -> verify(mockBookingRepository, times(0)).save(any(Booking.class))
        );
    }

    @Test
    void getBooking() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setBooker(user);
        when(mockBookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.getBooking(booking.getId(), user.getId());

        assertEquals(booking, foundBooking);
    }

    @Test
    void shouldThrowUserNotBookingCreatorOrItemOwnerExceptionAfterGetBooking() {
        Booking booking = generator.nextObject(Booking.class);
        when(mockBookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        UserNotBookingCreatorOrItemOwnerException userNotBookingCreatorOrItemOwnerException =
                assertThrows(
                        UserNotBookingCreatorOrItemOwnerException.class,
                        () -> bookingService.getBooking(booking.getId(), user.getId())
                );

        assertEquals(
                new UserNotBookingCreatorOrItemOwnerException(booking.getId(), user.getId()).getMessage(),
                userNotBookingCreatorOrItemOwnerException.getMessage()
        );
    }

    @Test
    void approveBooking() {
        Booking booking = generator.nextObject(Booking.class);
        booking.getItem().setOwner(user);
        booking.setStatus(BookingStatus.WAITING);

        when(mockBookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(mockBookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking approvedBooking = bookingService.approveBooking(booking.getId(), user.getId(), true);

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void shouldThrowUserNotItemOwnerInBookingExceptionAfterApproveBooking() {
        Booking booking = generator.nextObject(Booking.class);
        when(mockBookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        UserNotItemOwnerInBookingException userNotItemOwnerInBookingException =
                assertThrows(
                        UserNotItemOwnerInBookingException.class,
                        () -> bookingService.approveBooking(booking.getId(), user.getId(), true)
                );

        assertEquals(
                new UserNotItemOwnerInBookingException(booking.getId(), user.getId()).getMessage(),
                userNotItemOwnerInBookingException.getMessage()
        );
    }

    @Test
    void shouldThrowBadBookingStatusForApproveExceptionAfterApproveBooking() {
        Booking booking = generator.nextObject(Booking.class);
        booking.getItem().setOwner(user);
        booking.setStatus(BookingStatus.CANCELED);

        when(mockBookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BadBookingStatusForApproveException badBookingStatusForApproveException =
                assertThrows(
                        BadBookingStatusForApproveException.class,
                        () -> bookingService.approveBooking(booking.getId(), user.getId(), true)
                );

        assertEquals(
                "Статус бронирования " + booking.getId() + " уже изменен на " + BookingStatus.CANCELED,
                badBookingStatusForApproveException.getMessage()
        );
    }

    @Test
    void getAllBookingsOfUserByCurrentState() {
        List<Booking> bookings = generator.objects(Booking.class, 10).collect(Collectors.toList());
        when(mockBookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(
                user.getId(), BookingState.CURRENT, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByFutureState() {
        List<Booking> bookings = generator.objects(Booking.class, 10).collect(Collectors.toList());
        when(mockBookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                anyInt(), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(
                user.getId(), BookingState.FUTURE, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByPastState() {
        List<Booking> bookings = generator.objects(Booking.class, 10).collect(Collectors.toList());
        when(mockBookingRepository.findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
                anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(
                user.getId(), BookingState.PAST, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByWaitingState() {
        List<Booking> bookings = generator.objects(Booking.class, 10).collect(Collectors.toList());
        when(mockBookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                anyInt(), eq(BookingStatus.WAITING), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(
                user.getId(), BookingState.WAITING, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByRejectedState() {
        List<Booking> bookings = generator.objects(Booking.class, 10).collect(Collectors.toList());
        when(mockBookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                anyInt(), eq(BookingStatus.REJECTED), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(
                user.getId(), BookingState.REJECTED, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByAllState() {
        List<Booking> bookings = generator.objects(Booking.class, 10).collect(Collectors.toList());
        when(mockBookingRepository.findAllByBookerIdOrderByStartDesc(
                anyInt(), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(
                user.getId(), BookingState.ALL, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByCurrentState() {
        List<Booking> bookings = prepareDataForGetAllBookingsOfUserItems();
        when(mockBookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(
                user.getId(), BookingState.CURRENT, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByFutureState() {
        List<Booking> bookings = prepareDataForGetAllBookingsOfUserItems();
        when(mockBookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(
                anyList(), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(
                user.getId(), BookingState.FUTURE, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByPastState() {
        List<Booking> bookings = prepareDataForGetAllBookingsOfUserItems();
        when(mockBookingRepository.findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(
                anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(
                user.getId(), BookingState.PAST, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByWaitingState() {
        List<Booking> bookings = prepareDataForGetAllBookingsOfUserItems();
        when(mockBookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(
                anyList(), eq(BookingStatus.WAITING), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(
                user.getId(), BookingState.WAITING, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByRejectedState() {
        List<Booking> bookings = prepareDataForGetAllBookingsOfUserItems();
        when(mockBookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(
                anyList(), eq(BookingStatus.REJECTED), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(
                user.getId(), BookingState.REJECTED, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByAllState() {
        List<Booking> bookings = prepareDataForGetAllBookingsOfUserItems();
        when(mockBookingRepository.findAllByItemIdInOrderByStartDesc(
                anyList(), any(Pageable.class)
        )).thenReturn(bookings);

        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(
                user.getId(), BookingState.ALL, 0, 10
        );

        assertEquals(bookings, foundBookings);
    }

    private List<Booking> prepareDataForGetAllBookingsOfUserItems() {
        List<Booking> bookings = generator.objects(Booking.class, 10).collect(Collectors.toList());
        List<Item> items = generator.objects(Item.class, 10).collect(Collectors.toList());

        for (int i = 0; i < bookings.size(); i++) {
            items.get(i).setOwner(user);
            bookings.get(i).setItem(items.get(i));
        }
        when(mockItemRepository.findByOwnerId(anyInt(), any(Pageable.class)))
                .thenReturn(items);

        return bookings;
    }
}