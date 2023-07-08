package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
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
    public Item addItem(Item item, int ownerId) throws UserNotFoundException {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));

        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(
            Item item, int ownerId
    ) throws ItemNotFoundException, UserNotFoundException, UserNotOwnerOfItemException {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));
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
    public ItemBookingDetails getItem(int itemId, int userId) throws ItemNotFoundException, UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));

        if (user.getId() != item.getOwner().getId()) {
            return new ItemBookingDetails(item);
        } else {
            LocalDateTime currentDate = LocalDateTime.now();
            Optional<Booking> lastBookings = bookingRepository
                    .findLastByItemIdsAndItemOwnerIdAndStartIsBefore(List.of(item.getId()), user.getId(), currentDate)
                    .stream().findFirst();

            Optional<Booking> nextBookings = bookingRepository
                    .findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                            List.of(item.getId()), user.getId(), currentDate, List.of(BookingStatus.CANCELED, BookingStatus.REJECTED)
                    )
                    .stream().findFirst();

            return new ItemBookingDetails(item, lastBookings.orElse(null), nextBookings.orElse(null));
        }

    }

    @Override
    public List<ItemBookingDetails> getAllItemsByOwnerId(int ownerId) throws UserNotFoundException {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));
        List<Item> items = itemRepository.findByOwnerId(owner.getId());
        if (items.isEmpty())
            throw new ItemNotFoundException();

        LocalDateTime currentDate = LocalDateTime.now();
        List<Integer> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Integer, Booking> lastBookings = bookingRepository
                .findLastByItemIdsAndItemOwnerIdAndStartIsBefore(itemIds, owner.getId(), currentDate)
                .stream().collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));

        Map<Integer, Booking> nextBookings = bookingRepository
                .findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                        itemIds, owner.getId(), currentDate, List.of(BookingStatus.CANCELED, BookingStatus.REJECTED)
                )
                .stream().collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));

        return items.stream()
                .map(item ->
                        new ItemBookingDetails(item, lastBookings.get(item.getId()), nextBookings.get(item.getId()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAvailableItemsByText(String text) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        else
            return itemRepository.findAvailableByNameOrDescription(text);
    }
}
