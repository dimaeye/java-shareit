package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom(
            new EasyRandomParameters()
                    .randomize(Integer.class, new IntegerRangeRandomizer(0, 100))
    );

    private User user;
    private UserDTO requestUserDTO;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        user.setEmail("test@test.com");

        requestUserDTO = UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(requestUserDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserDTO responseUserDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserDTO.class
        );

        checkResult(user, responseUserDTO);
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(user);

        MvcResult result = mockMvc.perform(patch("/users/" + user.getId())
                        .content(objectMapper.writeValueAsString(requestUserDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserDTO responseUserDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserDTO.class
        );

        checkResult(user, responseUserDTO);
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser(anyInt())).thenReturn(user);

        MvcResult result = mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andReturn();
        UserDTO responseUserDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserDTO.class
        );

        checkResult(user, responseUserDTO);
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user));

        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        List<UserDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        checkResult(user, response.get(0));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(anyInt());

        mockMvc.perform(delete("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andReturn();
    }

    private void checkResult(User user, UserDTO userDTO) {
        assertAll(
                () -> assertEquals(user.getId(), userDTO.getId()),
                () -> assertEquals(user.getName(), userDTO.getName()),
                () -> assertEquals(user.getEmail(), userDTO.getEmail())
        );
    }
}