package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ItemServiceImplIT {

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    private final EasyRandom generator = new EasyRandom();

    private User owner;
    private User booker;


    @BeforeEach
    void beforeEach() {
        owner = generator.nextObject(User.class);
        owner.setId(0);
        entityManager.persist(owner);

        booker = generator.nextObject(User.class);
        booker.setId(0);
        entityManager.persist(booker);
    }

    @Test
    void getAllItemsByOwnerId() {
        int itemsCount = 10;
        Map<Integer, Item> items = createItems(itemsCount).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Integer, Booking> lastBookings = new HashMap<>();
        Map<Integer, Booking> nextBookings = new HashMap<>();
        Map<Integer, Comment> comments = new HashMap<>();
        items.keySet().forEach(itemId -> {
            lastBookings.put(
                    itemId,
                    createBooking(
                            LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1), items.get(itemId)
                    )
            );
            nextBookings.put(
                    itemId,
                    createBooking(
                            LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1), items.get(itemId)
                    )
            );
            comments.put(itemId, createComment(items.get(itemId)));
        });
        entityManager.flush();

        List<ItemBookingDetails> foundItems = itemService.getAllItemsByOwnerId(owner.getId(), 0, itemsCount);

        for (ItemBookingDetails itemBookingDetails : foundItems) {
            assertAll(
                    () -> assertEquals(
                            items.get(itemBookingDetails.getItem().getId()).getName(),
                            itemBookingDetails.getItem().getName()
                    ),
                    () -> assertEquals(
                            items.get(itemBookingDetails.getItem().getId()).getDescription(),
                            itemBookingDetails.getItem().getDescription()
                    ),
                    () -> assertEquals(
                            lastBookings.get(itemBookingDetails.getItem().getId()).getId(),
                            itemBookingDetails.getLastBooking().getId()
                    ),
                    () -> assertEquals(
                            nextBookings.get(itemBookingDetails.getItem().getId()).getId(),
                            itemBookingDetails.getNextBooking().getId()
                    ),
                    () -> assertEquals(
                            comments.get(itemBookingDetails.getItem().getId()).getId(),
                            itemBookingDetails.getComments().get(0).getId()
                    )
            );
        }

    }

    private List<Item> createItems(int size) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Item item = Item.builder()
                    .name(generator.nextObject(String.class))
                    .description(generator.nextObject(String.class))
                    .available(true)
                    .owner(owner)
                    .build();
            entityManager.persist(item);
            items.add(item);
        }
        return items;
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, Item item) {
        Booking booking = Booking.builder()
                .booker(booker)
                .item(item)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(booking);
        return booking;
    }

    private Comment createComment(Item item) {
        Comment comment = Comment.builder()
                .item(item)
                .text(generator.nextObject(String.class))
                .author(booker)
                .created(LocalDateTime.now())
                .build();
        entityManager.persist(comment);
        return comment;
    }
}