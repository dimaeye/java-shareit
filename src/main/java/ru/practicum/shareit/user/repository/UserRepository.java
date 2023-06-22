package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user) throws UserNotFoundException;

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void delete(User user) throws UserNotFoundException;
}
