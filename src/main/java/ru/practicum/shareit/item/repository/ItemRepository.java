package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item) throws ItemNotFoundException;

    Optional<Item> findById(int id);

    List<Item> findByOwnerId(int ownerId) throws UserNotFoundException;

    List<Item> findAvailableByNameOrDescription(String text);
}
