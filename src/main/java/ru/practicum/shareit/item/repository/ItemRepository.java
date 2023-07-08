package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(int ownerId) throws UserNotFoundException;

    @Query("FROM Item it " +
            "WHERE LOWER (it.name) LIKE LOWER (concat('%',:text,'%')) " +
            "OR LOWER (it.description) LIKE LOWER (concat('%',:text,'%')) " +
            "AND it.available = TRUE"
    )
    List<Item> findAvailableByNameOrDescription(String text);
}
