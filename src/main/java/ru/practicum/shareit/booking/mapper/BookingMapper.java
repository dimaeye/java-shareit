package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
    public static BookingDTO toBookingDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDTO(booking.getItem()))
                .booker(UserMapper.toUserDTO(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDTO bookingDTO) {
        return Booking.builder()
                .id(bookingDTO.getId())
                .start(bookingDTO.getStart())
                .end(bookingDTO.getEnd())
                .item(ItemMapper.toItem(bookingDTO.getItem()))
                .booker(UserMapper.toUser(bookingDTO.getBooker()))
                .status(bookingDTO.getStatus())
                .build();
    }
}