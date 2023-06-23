package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDTO toItemRequestDTO(ItemRequest itemRequest) {
        return ItemRequestDTO.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(UserMapper.toUserDTO(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDTO itemRequestDTO) {
        return ItemRequest.builder()
                .id(itemRequestDTO.getId())
                .description(itemRequestDTO.getDescription())
                .requestor(UserMapper.toUser(itemRequestDTO.getRequestor()))
                .created(itemRequestDTO.getCreated())
                .build();
    }
}
