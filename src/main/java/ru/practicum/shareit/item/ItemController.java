package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.annotation.AddItemConstraint;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.auth.AuthConstant.OWNER_ID_HEADER;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDTO addItem(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid @AddItemConstraint ItemDTO itemDTO
    ) {
        log.info("Запрос на добавление предмета - " + itemDTO + " владельца " + ownerId);
        Item addedItem = itemService.addItem(ItemMapper.toItem(itemDTO), ownerId);
        log.info("Предмет добавлен успешно");

        return ItemMapper.toItemDTO(addedItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDTO updateItem(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid ItemDTO itemDTO,
            @PathVariable("itemId") Integer itemId
    ) {
        itemDTO.setId(itemId);

        log.info("Запрос на обновление предмета - " + itemDTO + " владельца " + ownerId);
        Item updatedItem = itemService.updateItem(ItemMapper.toItem(itemDTO), ownerId);
        log.info("Предмет обновлен успешно");

        return ItemMapper.toItemDTO(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemById(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer userId,
            @PathVariable("itemId") Integer itemId
    ) {
        ItemBookingDetails itemBookingDetails = itemService.getItem(itemId, userId);

        return ItemMapper.toItemDTO(itemBookingDetails);
    }

    @GetMapping
    public List<ItemDTO> getAllItemsByOwnerId(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @PositiveOrZero Integer size
    ) {
        List<ItemBookingDetails> items = itemService.getAllItemsByOwnerId(ownerId, from, size);

        return items.stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDTO> getAvailableItemsByName(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @PositiveOrZero Integer size
    ) {
        List<Item> items = itemService.getAvailableItemsByText(text, from, size);

        return items.stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO addComment(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid CommentDTO commentDTO,
            @PathVariable Integer itemId
    ) {
        log.info("Запрос на добавление комментария к предмету - " + itemId + " пользователя " + ownerId);
        Comment addedComment = itemService.addComment(CommentMapper.toComment(commentDTO), itemId, ownerId);
        log.info("Комментарий добавлен успешно");

        return CommentMapper.toCommentDTO(addedComment);
    }
}
