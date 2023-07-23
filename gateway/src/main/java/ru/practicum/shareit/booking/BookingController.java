package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.user.auth.AuthConstant.OWNER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @RequestBody @Valid BookingRequestDTO bookingDTO
    ) {
        log.info("Создание бронирования {}, пользователя {}", bookingDTO, ownerId);
        return bookingClient.addBooking(ownerId, bookingDTO);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @PathVariable("bookingId") Integer bookingId,
            @RequestParam("approved") Boolean isApproved
    ) {
        log.info("Запрос на одобрение брони " + bookingId);
        return bookingClient.approveBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer ownerId,
            @PathVariable("bookingId") Integer bookingId
    ) {
        return bookingClient.getBooking(ownerId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsOfUserByState(
            @RequestHeader(OWNER_ID_HEADER)
            @Positive Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @Positive Integer size
    ) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

        return bookingClient.getAllBookingsOfUserByState(ownerId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOfUserItems(
            @RequestHeader(OWNER_ID_HEADER)
            @Positive Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @Positive Integer size
    ) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

        return bookingClient.getAllBookingsOfUserItems(ownerId, bookingState, from, size);
    }
}
