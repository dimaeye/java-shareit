package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        log.info("Запрос на создание пользователя - " + userDTO);
        User createdUser = userService.createUser(UserMapper.toUser(userDTO));
        log.info("Пользователь создан успешно");

        return UserMapper.toUserDTO(createdUser);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@RequestBody UserDTO userDTO, @PathVariable Integer userId) {
        userDTO.setId(userId);

        log.info("Запрос на обновление пользователя - " + userDTO);
        User updatedUser = userService.updateUser(UserMapper.toUser(userDTO));
        log.info("Пользователь обновлен успешно");

        return UserMapper.toUserDTO(updatedUser);
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable Integer userId) {
        User user = userService.getUser(userId);

        return UserMapper.toUserDTO(user);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return users.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Запрос на удаление пользователя id=" + userId);
        userService.deleteUser(userId);
        log.info("Пользователь удален успешно");
    }
}
