package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    private final ItemRequestRepository mockItemRequestRepository = mock(ItemRequestRepository.class);
    private final UserRepository mockUserRepository = mock(UserRepository.class);

    private final ItemRequestService itemRequestService = new ItemRequestServiceImpl(
            mockItemRequestRepository, mockUserRepository
    );

    private final EasyRandom generator = new EasyRandom();

    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(user));
    }

    @Test
    void addRequest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        when(mockItemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest addedItemRequest = itemRequestService.addRequest(itemRequest, user.getId());

        assertEquals(itemRequest, addedItemRequest);
    }

    @Test
    void getRequest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        when(mockItemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        ItemRequest foundItemRequest = itemRequestService.getRequest(itemRequest.getId(), user.getId());

        assertEquals(itemRequest, foundItemRequest);
    }

    @Test
    void getAllRequestsByRequestorId() {
        List<ItemRequest> itemRequests = generator.objects(ItemRequest.class, 10)
                .collect(Collectors.toList());
        when(mockItemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyInt()))
                .thenReturn(itemRequests);

        List<ItemRequest> foundItemRequests = itemRequestService.getAllRequestsByRequestorId(user.getId());

        assertEquals(itemRequests, foundItemRequests);
    }

    @Test
    void getAllRequests() {
        Page<ItemRequest> itemRequests = new PageImpl<>(
                generator.objects(ItemRequest.class, 10).collect(Collectors.toList())
        );
        when(mockItemRequestRepository.findByRequestorIdNot(anyInt(), any(Pageable.class))).thenReturn(itemRequests);

        List<ItemRequest> foundItemRequest = itemRequestService.getAllRequests(user.getId(), 0, 10);

        assertEquals(itemRequests.getContent(), foundItemRequest);
    }
}