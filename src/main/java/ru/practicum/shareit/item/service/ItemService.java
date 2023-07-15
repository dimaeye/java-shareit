package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserNotBookerOfItemException;
import ru.practicum.shareit.item.exception.UserNotOwnerOfItemException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, int ownerId) throws UserNotFoundException, ItemRequestNotFoundException;

    Item updateItem(
            Item item, int ownerId
    ) throws ItemNotFoundException, UserNotFoundException, UserNotOwnerOfItemException;

    ItemBookingDetails getItem(int itemId, int userId) throws ItemNotFoundException, UserNotFoundException;

    List<ItemBookingDetails> getAllItemsByOwnerId(int ownerId, int from, int size) throws UserNotFoundException;

    List<Item> getAvailableItemsByText(String text, int from, int size);

    Comment addComment(Comment comment, int itemId, int userId) throws UserNotBookerOfItemException;
}