package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserNotOwnerOfItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {
    private static final List<BookingStatus> CANCELED_BOOKING_STATUSES =
            List.of(BookingStatus.CANCELED, BookingStatus.REJECTED);

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(
            ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository
    ) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Item addItem(Item item, int ownerId) throws UserNotFoundException {
        User owner = getUser(ownerId);

        item.setOwner(owner);
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

        if (user.getId() != item.getOwner().getId()) {
            return new ItemBookingDetails(item);
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

            return new ItemBookingDetails(item, lastBooking.orElse(null), nextBooking.orElse(null));
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingDetails> getAllItemsByOwnerId(int ownerId) throws UserNotFoundException {
        User owner = getUser(ownerId);
        List<Item> items = itemRepository.findByOwnerId(owner.getId());
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

        return items.stream()
                .map(item ->
                        new ItemBookingDetails(
                                item,
                                lastBookings.getOrDefault(item.getId(), Collections.emptyList())
                                        .stream().findFirst().orElse(null),
                                nextBookings.getOrDefault(item.getId(), Collections.emptyList())
                                        .stream().findFirst().orElse(null)
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getAvailableItemsByText(String text) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        else
            return itemRepository.findAvailableByNameOrDescription(text);
    }

    private User getUser(int userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Item getItem(int itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }
}
