package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Transactional
class BookingServiceImplIT {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EntityManager entityManager;

    private final EasyRandom generator = new EasyRandom();

    private User booker;
    private Item item;

    @BeforeEach
    void beforeEach() {
        User owner = generator.nextObject(User.class);
        owner.setId(0);
        entityManager.persist(owner);

        booker = generator.nextObject(User.class);
        booker.setId(0);
        entityManager.persist(booker);

        item = Item.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item);
    }

    @Test
    void addBooking() {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertDoesNotThrow(
                () -> bookingService.addBooking(booking, booker.getId(), item.getId())
        );
    }
}