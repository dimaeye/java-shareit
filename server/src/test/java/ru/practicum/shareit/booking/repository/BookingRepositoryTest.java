package ru.practicum.shareit.booking.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final EasyRandom generator = new EasyRandom();
    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void beforeEach() {
        owner = generator.nextObject(User.class);
        owner.setId(0);
        owner = testEntityManager.persist(owner);

        booker = generator.nextObject(User.class);
        booker.setId(0);
        booker = testEntityManager.persist(booker);

        item = Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build();
        item = testEntityManager.persist(item);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        createBooking(
                LocalDateTime.now().plusMinutes(2), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );

        Booking savedBooking = createBooking(
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        booker.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged()
                );

        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        createBooking(
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(
                        booker.getId(), LocalDateTime.now(), Pageable.unpaged()
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc() {
        createBooking(
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusMinutes(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
                        booker.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged()
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    @Test
    void findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc() {
        createBooking(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMinutes(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                        List.of(item.getId()), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged()
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    @Test
    void findAllByItemIdInAndStartAfterOrderByStartDesc() {
        createBooking(
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findAllByItemIdInAndStartAfterOrderByStartDesc(
                        List.of(item.getId()), LocalDateTime.now(), Pageable.unpaged()
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    @Test
    void findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc() {
        createBooking(
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), BookingStatus.WAITING
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(
                        List.of(item.getId()), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged()
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }


    @Test
    void findAllByItemIdAndStartBetweenAndEndBetween() {
        createBooking(
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1).minusMinutes(1),
                BookingStatus.WAITING
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findAllByItemIdAndStartBetweenAndEndBetween(
                        item.getId(), LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(1)
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    @Test
    void findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn() {
        createBooking(
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                BookingStatus.REJECTED
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(
                        List.of(item.getId()), owner.getId(),
                        LocalDateTime.now(), List.of(BookingStatus.REJECTED)
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    @Test
    void findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn() {
        createBooking(
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusDays(1),
                BookingStatus.REJECTED
        );
        Booking savedBooking = createBooking(
                LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1), BookingStatus.WAITING
        );
        testEntityManager.flush();

        List<Booking> foundBookings = bookingRepository
                .findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                        List.of(item.getId()), owner.getId(),
                        LocalDateTime.now(), List.of(BookingStatus.REJECTED)
                );


        assertAll(
                () -> assertEquals(1, foundBookings.size()),
                () -> assertEquals(savedBooking.getId(), foundBookings.get(0).getId())
        );
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, BookingStatus bookingStatus) {
        Booking booking = Booking.builder()
                .booker(booker)
                .item(item)
                .start(start)
                .end(end)
                .status(bookingStatus)
                .build();

        return bookingRepository.save(booking);
    }
}