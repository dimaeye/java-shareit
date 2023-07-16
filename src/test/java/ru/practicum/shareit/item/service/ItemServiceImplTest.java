package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

    private final ItemService itemService = new ItemServiceImpl(
            itemRepository, userRepository, bookingRepository,
            commentRepository, itemRequestRepository
    );

    private final EasyRandom generator = new EasyRandom();

    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
    }

    @Test
    void addItem() {
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(generator.nextObject(ItemRequest.class)));
        Item item = generator.nextObject(Item.class);
        item.setRequestId(generator.nextInt());
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item addedItem = itemService.addItem(item, user.getId());

        assertAll(
                () -> assertEquals(item, addedItem),
                () -> verify(itemRequestRepository, times(1)).findById(anyInt())
        );
    }

    @Test
    void updateItem() {
        Item item = generator.nextObject(Item.class);
        item.setOwner(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updatedItem = itemService.updateItem(item, user.getId());

        assertEquals(item, updatedItem);
    }

    @Test
    void getItem() {
        Item item = generator.nextObject(Item.class);
        item.setOwner(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        List<Comment> comments = generator.objects(Comment.class, 10).collect(Collectors.toList());
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(comments);

        Booking lastBooking = generator.nextObject(Booking.class);
        when(bookingRepository.findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(
                anyList(), anyInt(), any(LocalDateTime.class), anyList()
        )).thenReturn(List.of(lastBooking));

        Booking nextBooking = generator.nextObject(Booking.class);
        when(bookingRepository.findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                anyList(), anyInt(), any(LocalDateTime.class), anyList()
        )).thenReturn(List.of(nextBooking));

        ItemBookingDetails itemBookingDetails = itemService.getItem(item.getId(), user.getId());

        assertAll(
                () -> assertEquals(item, itemBookingDetails.getItem()),
                () -> assertEquals(comments, itemBookingDetails.getComments()),
                () -> assertEquals(lastBooking, itemBookingDetails.getLastBooking()),
                () -> assertEquals(nextBooking, itemBookingDetails.getNextBooking())
        );

    }

    @Test
    void getAllItemsByOwnerId() {
        //TODO
    }

    @Test
    void getAvailableItemsByText() {
        List<Item> items = generator.objects(Item.class, 10).collect(Collectors.toList());
        when(itemRepository.findAvailableByNameOrDescription(anyString(), any(Pageable.class)))
                .thenReturn(items);

        List<Item> foundItems = itemService
                .getAvailableItemsByText(generator.nextObject(String.class), 0, 10);

        assertEquals(items, foundItems);
    }

    @Test
    void addComment() {
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBefore(anyInt(), anyInt(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        Comment comment = generator.nextObject(Comment.class);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment addedComment = itemService.addComment(comment, booking.getItem().getId(), booking.getBooker().getId());

        assertEquals(comment, addedComment);
    }
}