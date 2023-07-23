package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.annotation.AddItemConstraint;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.user.auth.AuthConstant.OWNER_ID_HEADER;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid @AddItemConstraint ItemDTO itemDTO
    ) {
        log.info("Запрос на добавление предмета - " + itemDTO + " владельца " + ownerId);
        return itemClient.addItem(ownerId, itemDTO);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid ItemDTO itemDTO,
            @PathVariable("itemId") Integer itemId
    ) {
        log.info("Запрос на обновление предмета - " + itemDTO + " владельца " + ownerId);
        return itemClient.updateItem(ownerId, itemId, itemDTO);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer userId,
            @PathVariable("itemId") Integer itemId
    ) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @Positive Integer size
    ) {
        return itemClient.getAllItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableItemsByName(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @Positive Integer size
    ) {
        return itemClient.getAvailableItemsByName(ownerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid CommentDTO commentDTO,
            @PathVariable Integer itemId
    ) {
        log.info("Запрос на добавление комментария к предмету - " + itemId + " пользователя " + ownerId);
        return itemClient.createComment(ownerId, itemId, commentDTO);
    }
}
