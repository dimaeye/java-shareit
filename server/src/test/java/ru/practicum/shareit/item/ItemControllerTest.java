package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.auth.AuthConstant;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    private Item item;
    private ItemBookingDetails itemBookingDetails;

    @BeforeEach
    void beforeEach() {
        item = generator.nextObject(Item.class);
        itemBookingDetails = generator.nextObject(ItemBookingDetails.class);
        itemBookingDetails.setItem(item);
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(Item.class), anyInt())).thenReturn(item);
        ItemDTO itemDTO = ItemDTO.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .build();

        MvcResult result = mockMvc.perform(post("/items")
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemDTO responseItemDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), ItemDTO.class
        );

        checkResult(item, responseItemDTO);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(Item.class), anyInt())).thenReturn(item);
        ItemDTO itemDTO = ItemDTO.builder()
                .name(generator.nextObject(String.class))
                .description(generator.nextObject(String.class))
                .available(true)
                .build();

        MvcResult result = mockMvc.perform(patch("/items/" + item.getId())
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemDTO responseItemDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), ItemDTO.class
        );

        checkResult(item, responseItemDTO);
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItem(anyInt(), anyInt())).thenReturn(itemBookingDetails);

        MvcResult result = mockMvc.perform(get("/items/" + item.getId())
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();
        ItemDTO responseItemDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), ItemDTO.class
        );

        checkResult(itemBookingDetails, responseItemDTO);
    }

    @Test
    void getAllItemsByOwnerId() throws Exception {
        when(itemService.getAllItemsByOwnerId(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemBookingDetails));

        MvcResult result = mockMvc.perform(get("/items")
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();

        List<ItemDTO> responseItemDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );

        checkResult(itemBookingDetails, responseItemDTO.get(0));
    }

    @Test
    void getAvailableItemsByName() throws Exception {
        when(itemService.getAvailableItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        MvcResult result = mockMvc.perform(get("/items/search")
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .param("text", generator.nextObject(String.class)))
                .andExpect(status().isOk())
                .andReturn();

        List<ItemDTO> responseItemDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );

        checkResult(item, responseItemDTO.get(0));
    }

    @Test
    void addComment() throws Exception {
        Comment comment = generator.nextObject(Comment.class);
        when(itemService.addComment(any(Comment.class), anyInt(), anyInt()))
                .thenReturn(comment);

        CommentDTO commentDTO = CommentDTO.builder()
                .text(comment.getText())
                .build();

        MvcResult result = mockMvc.perform(post("/items/" + item.getId() + "/comment")
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(commentDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        CommentDTO responseCommentDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), CommentDTO.class
        );

        assertAll(
                () -> assertEquals(comment.getId(), responseCommentDTO.getId()),
                () -> assertEquals(comment.getText(), responseCommentDTO.getText()),
                () -> assertEquals(comment.getAuthor().getName(), responseCommentDTO.getAuthorName())
        );

    }

    private void checkResult(Item item, ItemDTO itemDTO) {
        assertAll(
                () -> assertEquals(item.getId(), itemDTO.getId()),
                () -> assertEquals(item.getName(), itemDTO.getName()),
                () -> assertEquals(item.getDescription(), itemDTO.getDescription()),
                () -> assertEquals(item.getAvailable(), itemDTO.getAvailable())
        );
    }

    private void checkResult(ItemBookingDetails itemBookingDetails, ItemDTO itemDTO) {
        assertAll(
                () -> checkResult(itemBookingDetails.getItem(), itemDTO),
                () -> assertEquals(itemBookingDetails.getLastBooking().getId(), itemDTO.getLastBooking().getId()),
                () -> assertEquals(itemBookingDetails.getNextBooking().getId(), itemDTO.getNextBooking().getId()),
                () -> assertEquals(itemBookingDetails.getComments().size(), itemDTO.getComments().size())
        );
    }
}