package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIT {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private EntityManager entityManager;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void addRequest() {
        User user = generator.nextObject(User.class);
        user.setId(0);
        entityManager.persist(user);

        ItemRequest itemRequest = ItemRequest.builder()
                .description(generator.nextObject(String.class))
                .created(LocalDateTime.now())
                .build();

        assertDoesNotThrow(
                () -> itemRequestService.addRequest(itemRequest, user.getId())
        );
    }
}