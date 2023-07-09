package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingDetails;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

@UtilityClass
public class ItemMapper {
    public static ItemDTO toItemDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? ItemRequestMapper.toItemRequestDTO(item.getRequest()) : null)
                .build();
    }

    public static ItemDTO toItemDTO(ItemBookingDetails itemBookingDetails) {
        ItemDTO itemDTO = toItemDTO(itemBookingDetails.getItem());

        if (itemBookingDetails.getLastBooking() != null) {
            BookingDTO lastBooking = BookingMapper.toBookingDTO(itemBookingDetails.getLastBooking());
            lastBooking.setItem(null);
            lastBooking.setBookerId(lastBooking.getBooker().getId());
            lastBooking.setBooker(null);
            itemDTO.setLastBooking(lastBooking);
        }

        if (itemBookingDetails.getNextBooking() != null) {
            BookingDTO nextBooking = BookingMapper.toBookingDTO(itemBookingDetails.getNextBooking());
            nextBooking.setItem(null);
            nextBooking.setBookerId(nextBooking.getBooker().getId());
            nextBooking.setBooker(null);
            itemDTO.setNextBooking(nextBooking);
        }

        return itemDTO;
    }

    public static Item toItem(ItemDTO itemDTO) {
        return Item.builder()
                .id(itemDTO.getId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .request(itemDTO.getRequest() != null ? ItemRequestMapper.toItemRequest(itemDTO.getRequest()) : null)
                .build();
    }
}
