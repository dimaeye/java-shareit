package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserNotOwnerOfItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
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

        if (!getItem(item.getId()).getOwner().equals(owner))
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
    public Item getItem(int itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Override
    public List<Item> getAllItemsByOwnerId(int ownerId) throws UserNotFoundException {
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Item> getAvailableItemsByText(String text) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        else
            return itemRepository.findAvailableByNameOrDescription(text);
    }
}
