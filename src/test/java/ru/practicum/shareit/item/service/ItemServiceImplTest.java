package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    private final ItemRepository mockItemRepository = mock(ItemRepository.class);
    private final UserRepository mockUserRepository = mock(UserRepository.class);
    private final BookingRepository mockBookingRepository = mock(BookingRepository.class);
    private final CommentRepository mockCommentRepository = mock(CommentRepository.class);
    private final ItemRequestRepository mockItemRequestRepository = mock(ItemRequestRepository.class);

    private final ItemService itemService = new ItemServiceImpl(
            mockItemRepository, mockUserRepository, mockBookingRepository,
            mockCommentRepository, mockItemRequestRepository
    );

    private final EasyRandom generator = new EasyRandom();

    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));
    }

    @Test
    void addItem() {
        when(mockItemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(generator.nextObject(ItemRequest.class)));
        Item item = generator.nextObject(Item.class);
        item.setRequestId(generator.nextInt());
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);

        Item addedItem = itemService.addItem(item, user.getId());

        assertAll(
                () -> assertEquals(item, addedItem),
                () -> verify(mockItemRequestRepository, times(1)).findById(anyInt())
        );
    }

    @Test
    void updateItem() {
        Item item = generator.nextObject(Item.class);
        item.setOwner(user);
        when(mockItemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);

        Item updatedItem = itemService.updateItem(item, user.getId());

        assertEquals(item, updatedItem);
    }

    @Test
    void getItem() {
        Item item = generator.nextObject(Item.class);
        item.setOwner(user);
        when(mockItemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        List<Comment> comments = generator.objects(Comment.class, 10).collect(Collectors.toList());
        when(mockCommentRepository.findAllByItemId(anyInt())).thenReturn(comments);

        Booking lastBooking = generator.nextObject(Booking.class);
        when(mockBookingRepository.findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(
                anyList(), anyInt(), any(LocalDateTime.class), anyList()
        )).thenReturn(List.of(lastBooking));

        Booking nextBooking = generator.nextObject(Booking.class);
        when(mockBookingRepository.findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
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
        int itemId = generator.nextInt();

        List<Item> items = generator.objects(Item.class, 10).collect(Collectors.toList());
        items.get(0).setId(itemId);
        when(mockItemRepository.findByOwnerId(anyInt(), any(Pageable.class))).thenReturn(items);

        List<Booking> lastBookings = generator.objects(Booking.class, 2).collect(Collectors.toList());
        lastBookings.forEach(b -> b.setItem(items.get(0)));
        when(mockBookingRepository.findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(
                anyList(), anyInt(), any(LocalDateTime.class), anyList()
        )).thenReturn(lastBookings);

        List<Booking> nextBookings = generator.objects(Booking.class, 2).collect(Collectors.toList());
        nextBookings.forEach(b -> b.setItem(items.get(0)));
        when(mockBookingRepository.findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                anyList(), anyInt(), any(LocalDateTime.class), anyList()
        )).thenReturn(nextBookings);

        List<Comment> comments = generator.objects(Comment.class, 10).collect(Collectors.toList());
        comments.forEach(c -> c.setItem(items.get(0)));
        when(mockCommentRepository.findAllByItemIdInOrderByIdAsc(anyList())).thenReturn(comments);

        List<ItemBookingDetails> itemBookingDetails = itemService.getAllItemsByOwnerId(user.getId(), 0, 10);

        ItemBookingDetails itemDetails =
                itemBookingDetails.stream().filter(i -> i.getItem().getId() == itemId).findFirst().get();
        assertAll(
                () -> assertEquals(lastBookings.stream().findFirst().get(), itemDetails.getLastBooking()),
                () -> assertEquals(nextBookings.stream().findFirst().get(), itemDetails.getNextBooking()),
                () -> assertEquals(comments, itemDetails.getComments())
        );
    }

    @Test
    void shouldThrowItemNotFoundExceptionAfterItemsNotFoundByOwner() {
        when(mockItemRepository.findByOwnerId(anyInt(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        ItemNotFoundException itemNotFoundException = assertThrows(
                ItemNotFoundException.class, () -> itemService.getAllItemsByOwnerId(user.getId(), 0, 10)
        );

        assertEquals(new ItemNotFoundException().getMessage(), itemNotFoundException.getMessage());
    }

    @Test
    void getAvailableItemsByText() {
        List<Item> items = generator.objects(Item.class, 10).collect(Collectors.toList());
        when(mockItemRepository.findAvailableByNameOrDescription(anyString(), any(Pageable.class)))
                .thenReturn(items);

        List<Item> foundItems = itemService
                .getAvailableItemsByText(generator.nextObject(String.class), 0, 10);

        assertEquals(items, foundItems);
    }

    @Test
    void shouldReturnEmptyListOfItemsWhenSearchTextIsBlank() {
        List<Item> foundItems = itemService
                .getAvailableItemsByText(" ", 0, 10);

        assertTrue(foundItems.isEmpty());
    }

    @Test
    void addComment() {
        Booking booking = generator.nextObject(Booking.class);
        when(mockBookingRepository.findFirstByItemIdAndBookerIdAndEndIsBefore(anyInt(), anyInt(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        Comment comment = generator.nextObject(Comment.class);
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment addedComment = itemService.addComment(comment, booking.getItem().getId(), booking.getBooker().getId());

        assertEquals(comment, addedComment);
    }
}