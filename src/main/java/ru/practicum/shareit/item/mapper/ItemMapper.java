package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

public class ItemMapper {
    public static ItemDTO toItemDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? ItemRequestMapper.toItemRequestDTO(item.getRequest()) : null)
                .build();
    }

    public static Item toItem(ItemDTO itemDTO) {
        return Item.builder()
                .id(itemDTO.getId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .request(itemDTO.getRequest() != null ? ItemRequestMapper.toItemRequest(itemDTO.getRequest()) : null)
                .build();
    }
}
