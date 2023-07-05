package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
@AllArgsConstructor
public class Item {
    private int id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private ItemRequest request;
}
