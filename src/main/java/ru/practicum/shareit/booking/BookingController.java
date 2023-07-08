package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.auth.AuthConstant.OWNER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;


    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDTO addBooking(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid BookingDTO bookingDTO
    ) {
        Booking addedBooking = bookingService.addBooking(
                BookingMapper.toBooking(bookingDTO), ownerId, bookingDTO.getItemId()
        );

        return BookingMapper.toBookingDTO(addedBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDTO approveBooking(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @PathVariable("bookingId") Integer bookingId,
            @RequestParam("approved") Boolean isApproved
    ) {
        Booking booking = bookingService.approveBooking(bookingId, ownerId, isApproved);

        return BookingMapper.toBookingDTO(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDTO getBooking(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @PathVariable("bookingId") Integer bookingId
    ) {
        Booking booking = bookingService.getBooking(bookingId, ownerId);

        return BookingMapper.toBookingDTO(booking);
    }

    @GetMapping
    public List<BookingDTO> getAllBookingsOfUserByState(
            @RequestHeader(OWNER_ID_HEADER)
            @Positive
            Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL")
            String state
    ) {
        List<Booking> allBookings = bookingService.getAllBookingsOfUserByState(ownerId, getBookingState(state));

        return allBookings.stream().map(BookingMapper::toBookingDTO).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDTO> getAllBookingsOfUserItems(
            @RequestHeader(OWNER_ID_HEADER)
            @Positive
            Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL")
            String state
    ) {
        List<Booking> allBookings = bookingService.getAllBookingsOfUserItems(ownerId, getBookingState(state));

        return allBookings.stream().map(BookingMapper::toBookingDTO).collect(Collectors.toList());
    }


    private BookingState getBookingState(String state) throws ValidationException {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new ValidationException("Unknown state: " + state);
        }

        return bookingState;
    }

}
