package ru.practicum.shareit.item.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final EasyRandom generator = new EasyRandom();

    @Test
    void findAvailableByNameOrDescription() {
        User user = generator.nextObject(User.class);
        user.setId(0);
        User savedUser = entityManager.persist(user);

        Item item = generator.nextObject(Item.class);
        item.setOwner(savedUser);
        item.setAvailable(true);
        item.setRequest(null);
        Item savedItem = itemRepository.save(item);

        List<Item> foundItems = itemRepository.findAvailableByNameOrDescription(
                savedItem.getDescription().substring(0, savedItem.getDescription().length() - 1), Pageable.unpaged()
        );

        assertEquals(savedItem.getId(), foundItems.get(0).getId());
    }
}