package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final UserRepository mockUserRepository = mock(UserRepository.class);

    private final UserService userService = new UserServiceImpl(mockUserRepository);

    private final EasyRandom generator = new EasyRandom();

    @Test
    void createUser() {
        User user = generator.nextObject(User.class);
        when(mockUserRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals(user, createdUser);
    }

    @Test
    void updateUser() {
        User userForUpdate = generator.nextObject(User.class);
        when(mockUserRepository.findById(anyInt())).thenReturn(Optional.of(userForUpdate));
        when(mockUserRepository.save(any(User.class))).thenAnswer(i ->
                Arrays.stream(i.getArguments()).findFirst().get()
        );

        User updatedUser = userService.updateUser(generator.nextObject(User.class));

        assertEquals(userForUpdate, updatedUser);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenTryToUpdateUnknownUser() {
        when(mockUserRepository.findById(anyInt())).thenReturn(Optional.empty());

        User user = generator.nextObject(User.class);

        UserNotFoundException userNotFoundException =
                assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));

        assertEquals(new UserNotFoundException(user.getId()).getMessage(), userNotFoundException.getMessage());
    }

    @Test
    void getUser() {
        User user = generator.nextObject(User.class);
        when(mockUserRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User foundUser = userService.getUser(user.getId());

        assertEquals(user, foundUser);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenTryToGetUnknownUser() {
        when(mockUserRepository.findById(anyInt())).thenReturn(Optional.empty());
        int userId = generator.nextInt();

        UserNotFoundException userNotFoundException =
                assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));

        assertEquals(new UserNotFoundException(userId).getMessage(), userNotFoundException.getMessage());
    }

    @Test
    void getAllUsers() {
        List<User> users = generator.objects(User.class, 10).collect(Collectors.toList());
        when(mockUserRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.getAllUsers();

        assertEquals(users, foundUsers);
    }

    @Test
    void deleteUser() {
        User user = generator.nextObject(User.class);
        when(mockUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(mockUserRepository).delete(any(User.class));

        assertAll(
                () -> assertDoesNotThrow(() -> userService.deleteUser(user.getId())),
                () -> verify(mockUserRepository, times(1)).delete(any(User.class))
        );
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenTryToDeleteUnknownUser() {
        when(mockUserRepository.findById(anyInt())).thenReturn(Optional.empty());
        int userId = generator.nextInt();

        UserNotFoundException userNotFoundException =
                assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals(new UserNotFoundException(userId).getMessage(), userNotFoundException.getMessage());
    }
}