package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDTO toItemRequestDTO(ItemRequest itemRequest) {
        return ItemRequestDTO.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(
                        itemRequest.getItems() != null ?
                                itemRequest.getItems().stream()
                                        .map(item ->
                                                ItemDTO.builder()
                                                        .id(item.getId())
                                                        .name(item.getName())
                                                        .description(item.getDescription())
                                                        .requestId(item.getRequestId())
                                                        .available(item.getAvailable())
                                                        .build()
                                        ).collect(Collectors.toList()) : Collections.emptyList()
                )
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDTO itemRequestDTO) {
        return ItemRequest.builder()
                .id(itemRequestDTO.getId())
                .description(itemRequestDTO.getDescription())
                .created(itemRequestDTO.getCreated())
                .build();
    }
}
