package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDTO {
    private int id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDTO item;

    private UserDTO booker;

    private BookingStatus status;
}
