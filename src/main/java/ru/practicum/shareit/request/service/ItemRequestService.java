package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(ItemRequest itemRequest, int requestorId) throws UserNotFoundException;

    ItemRequest getRequest(
            int requestId, int requestorId
    ) throws ItemRequestNotFoundException, UserNotFoundException;

    List<ItemRequest> getAllRequestsByRequestorId(int requestorId);

    List<ItemRequest> getAllRequests(int requestorId, int from, int size);
}
