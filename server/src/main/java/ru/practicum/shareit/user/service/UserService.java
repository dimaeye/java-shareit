package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user) throws UserNotFoundException;

    User getUser(int userId) throws UserNotFoundException;

    List<User> getAllUsers();

    void deleteUser(int userId) throws UserNotFoundException;
}
