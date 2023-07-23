package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDTO {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private BookingDTO lastBooking;
    private BookingDTO nextBooking;
    private List<CommentDTO> comments;
}
