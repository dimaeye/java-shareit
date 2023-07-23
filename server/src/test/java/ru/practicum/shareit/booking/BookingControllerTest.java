package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.exception.BadBookingStatusForApproveException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.auth.AuthConstant;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    private Booking booking;

    @BeforeEach
    void beforeEach() {
        booking = generator.nextObject(Booking.class);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(any(Booking.class), anyInt(), anyInt())).thenReturn(booking);

        BookingDTO requestBookingDTO = BookingDTO.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();

        MvcResult result = mockMvc.perform(post("/bookings")
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(requestBookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingDTO responseBookingDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDTO.class
        );

        checkResult(booking, responseBookingDTO);
    }

    @Test
    void getBadRequestAfterTryToAddBookingWithPastEndTime() throws Exception {
        BookingDTO requestBookingDTO = BookingDTO.builder()
                .start(booking.getStart())
                .end(LocalDateTime.now().minusDays(1))
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();

        mockMvc.perform(post("/bookings")
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .content(objectMapper.writeValueAsString(requestBookingDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().name()));
    }

    @Test
    void shouldReturnBadRequestAfterThrowBadBookingStatusForApproveExceptionWhenApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new BadBookingStatusForApproveException(generator.nextObject(String.class)));

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(AuthConstant.OWNER_ID_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt())).thenReturn(booking);

        MvcResult result = mockMvc.perform(get("/bookings/" + booking.getId())
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();
        BookingDTO responseBookingDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDTO.class
        );

        checkResult(booking, responseBookingDTO);
    }

    @Test
    void shouldReturn404AfterThrowBookingNotFoundExceptionWhenGetBooking() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenThrow(new BookingNotFoundException());

        mockMvc.perform(get("/bookings/" + booking.getId())
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingsOfUserByState() throws Exception {
        when(bookingService.getAllBookingsOfUserByState(anyInt(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        MvcResult result = mockMvc.perform(get("/bookings")
                        .param("state", BookingState.ALL.name())
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );

        checkResult(booking, response.get(0));
    }

    @Test
    void getAllBookingsOfUserItems() throws Exception {
        when(bookingService.getAllBookingsOfUserItems(anyInt(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        MvcResult result = mockMvc.perform(get("/bookings/owner")
                        .param("state", BookingState.ALL.name())
                        .header(AuthConstant.OWNER_ID_HEADER, 1))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDTO> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );

        checkResult(booking, response.get(0));
    }

    private void checkResult(Booking booking, BookingDTO bookingDTO) {
        assertAll(
                () -> assertEquals(booking.getId(), bookingDTO.getId()),
                () -> assertEquals(booking.getBooker().getId(), bookingDTO.getBooker().getId()),
                () -> assertEquals(booking.getItem().getId(), bookingDTO.getItem().getId())
        );
    }
}