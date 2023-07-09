package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemBookingDetails {
    @NonNull
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
    @NonNull
    private List<Comment> comments;
}
