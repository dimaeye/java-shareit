package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.auth.AuthConstant;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemRequest = generator.nextObject(ItemRequest.class);
    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.addRequest(any(ItemRequest.class), anyInt()))
                .thenReturn(itemRequest);

        ItemRequestDTO itemRequestDTO = ItemRequestDTO.builder()
                .description(generator.nextObject(String.class))
                .requestorId(generator.nextInt())
                .build();

        MvcResult result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDTO))
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestDTO responseItemRequestDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ItemRequestDTO.class
        );

        assertAll(
                () -> assertEquals(itemRequest.getId(), responseItemRequestDTO.getId()),
                () -> assertEquals(itemRequest.getDescription(), responseItemRequestDTO.getDescription())
        );
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestService.getRequest(anyInt(), anyInt())).thenReturn(itemRequest);

        MvcResult result = mockMvc.perform(get("/requests/" + itemRequest.getId())
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestDTO responseItemRequestDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ItemRequestDTO.class
        );

        checkResultOfGetRequests(itemRequest, responseItemRequestDTO);
    }

    @Test
    void getAllRequestsByRequestorId() throws Exception {
        when(itemRequestService.getAllRequestsByRequestorId(anyInt()))
                .thenReturn(List.of(itemRequest));

        MvcResult result = mockMvc.perform(get("/requests")
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();
        List<ItemRequestDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ItemRequestDTO>>() {
                }
        );

        checkResultOfGetRequests(itemRequest, response.get(0));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest));

        MvcResult result = mockMvc.perform(get("/requests/all")
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();
        List<ItemRequestDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ItemRequestDTO>>() {
                }
        );

        checkResultOfGetRequests(itemRequest, response.get(0));
    }

    private void checkResultOfGetRequests(ItemRequest itemRequest, ItemRequestDTO itemRequestDTO) {
        assertAll(
                () -> assertEquals(itemRequest.getId(), itemRequestDTO.getId()),
                () -> assertEquals(itemRequest.getDescription(), itemRequestDTO.getDescription()),
                () -> assertEquals(
                        itemRequest.getItems().stream().map(Item::getId)
                                .sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList()),
                        itemRequestDTO.getItems().stream().map(ItemDTO::getId)
                                .sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList())
                )
        );
    }
}