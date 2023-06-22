package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.annotation.AddItemConstraint;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {

    public static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
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
    public ItemDTO getItemById(@PathVariable("itemId") Integer itemId) {
        Item item = itemService.getItem(itemId);

        return ItemMapper.toItemDTO(item);
    }

    @GetMapping
    public List<ItemDTO> getAllItemsByOwnerId(@RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId) {
        List<Item> items = itemService.getAllItemsByOwnerId(ownerId);

        return items.stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDTO> getAvailableItemsByName(@RequestParam String text) {
        List<Item> items = itemService.getAvailableItemsByText(text);

        return items.stream().map(ItemMapper::toItemDTO).collect(Collectors.toList());
    }
}
