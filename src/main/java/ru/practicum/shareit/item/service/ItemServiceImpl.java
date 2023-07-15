package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserNotBookerOfItemException;
import ru.practicum.shareit.item.exception.UserNotOwnerOfItemException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final List<BookingStatus> CANCELED_BOOKING_STATUSES =
            List.of(BookingStatus.CANCELED, BookingStatus.REJECTED);

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public Item addItem(Item item, int ownerId) throws UserNotFoundException, ItemRequestNotFoundException {
        User owner = getUser(ownerId);
        item.setOwner(owner);

        if (item.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository
                    .findById(item.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException(item.getRequestId()));
            item.setRequest(itemRequest);
        }

        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(
            Item item, int ownerId
    ) throws ItemNotFoundException, UserNotFoundException, UserNotOwnerOfItemException {
        User owner = getUser(ownerId);
        Item itemForUpdate = itemRepository
                .findById(item.getId()).orElseThrow(() -> new ItemNotFoundException(item.getId()));

        if (!itemForUpdate.getOwner().equals(owner))
            throw new UserNotOwnerOfItemException("Предмет " + item + "не принадлежит пользователю " + owner);
        else {
            if (item.getName() != null)
                itemForUpdate.setName(item.getName());
            if (item.getDescription() != null)
                itemForUpdate.setDescription(item.getDescription());
            if (item.getAvailable() != null)
                itemForUpdate.setAvailable(item.getAvailable());
            return itemRepository.save(itemForUpdate);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBookingDetails getItem(int itemId, int userId) throws ItemNotFoundException, UserNotFoundException {
        User user = getUser(userId);
        Item item = getItem(itemId);

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        if (user.getId() != item.getOwner().getId()) {
            return new ItemBookingDetails(item, comments);
        } else {
            LocalDateTime currentDate = LocalDateTime.now();

            Optional<Booking> lastBooking = bookingRepository
                    .findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(
                            List.of(item.getId()), user.getId(), currentDate, CANCELED_BOOKING_STATUSES
                    )
                    .stream().findFirst();

            Optional<Booking> nextBooking = bookingRepository
                    .findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                            List.of(item.getId()), user.getId(), currentDate, CANCELED_BOOKING_STATUSES
                    )
                    .stream().findFirst();


            return new ItemBookingDetails(
                    item, lastBooking.orElse(null), nextBooking.orElse(null), comments
            );
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingDetails> getAllItemsByOwnerId(int ownerId, int from, int size) throws UserNotFoundException {
        if (size <= 0)
            throw new IllegalArgumentException("Размер не должен быть меньше единицы.");

        Pageable pageable = PageRequest.of(from / size, size);

        User owner = getUser(ownerId);
        List<Item> items = itemRepository.findByOwnerId(owner.getId(), pageable);
        if (items.isEmpty())
            throw new ItemNotFoundException();

        LocalDateTime currentDate = LocalDateTime.now();
        List<Integer> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Integer, List<Booking>> lastBookings = bookingRepository
                .findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(
                        itemIds, owner.getId(), currentDate, CANCELED_BOOKING_STATUSES
                )
                .stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Integer, List<Booking>> nextBookings = bookingRepository
                .findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                        itemIds, owner.getId(), currentDate, CANCELED_BOOKING_STATUSES
                )
                .stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Integer, List<Comment>> comments = commentRepository
                .findAllByItemIdInOrderByIdAsc(itemIds)
                .stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));

        return items.stream()
                .map(item ->
                        new ItemBookingDetails(
                                item,
                                lastBookings.getOrDefault(item.getId(), Collections.emptyList())
                                        .stream().findFirst().orElse(null),
                                nextBookings.getOrDefault(item.getId(), Collections.emptyList())
                                        .stream().findFirst().orElse(null),
                                comments.getOrDefault(item.getId(), Collections.emptyList())
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getAvailableItemsByText(String text, int from, int size) {
        if (size <= 0)
            throw new IllegalArgumentException("Размер не должен быть меньше единицы.");

        if (text == null || text.isBlank())
            return Collections.emptyList();
        else {
            Pageable pageable = PageRequest.of(from / size, size);
            return itemRepository.findAvailableByNameOrDescription(text, pageable);
        }
    }

    @Override
    @Transactional
    public Comment addComment(Comment comment, int itemId, int userId) throws UserNotBookerOfItemException {
        LocalDateTime currentDate = LocalDateTime.now();

        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBefore(
                itemId, userId, currentDate
        ).orElseThrow(() -> new UserNotBookerOfItemException("Пользователь не брал вещь в аренду"));

        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        comment.setCreated(currentDate);

        return commentRepository.save(comment);
    }

    private User getUser(int userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Item getItem(int itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }
}
