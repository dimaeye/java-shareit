package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDTO {
    private int id;
    @Pattern(regexp = "^(?!\\s*$).+", message = "ItemName can not be empty")
    private String name;
    @Pattern(regexp = "^(?!\\s*$).+", message = "UserDescription can not be empty")
    private String description;
    private Boolean available;
    private ItemRequestDTO request;
    private BookingDTO lastBooking;
    private BookingDTO nextBooking;
    private List<CommentDTO> comments;
}
