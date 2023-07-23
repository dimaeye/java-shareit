package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequest addRequest(ItemRequest itemRequest, int requestorId) throws UserNotFoundException {
        User requestor = getUser(requestorId);

        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequest getRequest(
            int requestId, int requestorId
    ) throws ItemRequestNotFoundException, UserNotFoundException {
        getUser(requestorId);

        return itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new ItemNotFoundException(requestId));
    }

    @Override
    public List<ItemRequest> getAllRequestsByRequestorId(int requestorId) {
        getUser(requestorId);

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
    }

    @Override
    public List<ItemRequest> getAllRequests(int requestorId, int from, int size) {
        return itemRequestRepository
                .findByRequestorIdNot(requestorId, PageRequest.of(from, size, Sort.by("created").descending()))
                .getContent();
    }

    private User getUser(int userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
