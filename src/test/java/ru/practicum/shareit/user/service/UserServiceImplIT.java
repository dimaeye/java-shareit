package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class UserServiceImplIT {

    @Autowired
    UserService userService;


    @Test
    void updateUser() {
        User user = User.builder()
                .name("test")
                .email("test@test.com")
                .build();
        int createdUserId = userService.createUser(user).getId();
        user.setId(createdUserId);

        user.setName("test2");
        User updatedUser = userService.updateUser(user);

        assertEquals(user.getName(), updatedUser.getName());
    }

}