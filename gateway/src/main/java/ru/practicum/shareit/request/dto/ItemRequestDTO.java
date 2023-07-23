package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDTO;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDTO {
    private int id;
    @NotBlank
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
    private List<ItemDTO> items;
}
