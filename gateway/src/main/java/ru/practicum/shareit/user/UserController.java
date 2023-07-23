package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.annotation.CreateUserConstraint;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody @CreateUserConstraint UserDTO userDTO) {
        log.info("Запрос на создание пользователя - " + userDTO);
        return userClient.createUser(userDTO);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @Valid @RequestBody UserDTO userDTO, @PathVariable @Positive Integer userId
    ) {
        log.info("Запрос на обновление пользователя - " + userDTO);
        return userClient.updateUser(userId, userDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive Integer userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Integer userId) {
        log.info("Запрос на удаление пользователя id=" + userId);
        return userClient.deleteUser(userId);
    }
}
