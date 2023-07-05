package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDTO {
    private int id;
    private String description;
    private UserDTO requestor;
    private LocalDateTime created;
}
