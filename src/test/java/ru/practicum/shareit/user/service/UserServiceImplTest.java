package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);

    private final UserService userService = new UserServiceImpl(userRepository);

    private final EasyRandom generator = new EasyRandom();

    @Test
    void createUser() {
        User user = generator.nextObject(User.class);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals(user, createdUser);
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void deleteUser() {
    }
}