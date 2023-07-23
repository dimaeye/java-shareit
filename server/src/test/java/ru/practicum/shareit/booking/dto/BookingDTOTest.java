package ru.practicum.shareit.booking.dto;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BookingDTOTest {
    @Autowired
    private JacksonTester<BookingDTO> json;

    private final EasyRandom generator = new EasyRandom();
    private final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    @Test
    void testBookingDTO() throws IOException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .id(generator.nextInt())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(generator.nextInt())
                .status(BookingStatus.WAITING)
                .booker(generator.nextObject(UserDTO.class))
                .build();

        JsonContent<BookingDTO> result = json.write(bookingDTO);

        assertAll(
                () -> assertThat(result).extractingJsonPathValue("$.bookerId").isNull(),
                () -> assertThat(result).extractingJsonPathValue("$.id").isEqualTo(bookingDTO.getId()),
                () -> assertThat(result).extractingJsonPathValue("$.start")
                        .isEqualTo(DateTimeFormatter.ofPattern(dateTimeFormat).format(bookingDTO.getStart()))
        );
    }

    @Test
    void testJsonBookingDTO() throws IOException {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(2);
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        String jsonBody = "{\n" +
                "    \"itemId\": 2,\n" +
                "    \"start\": \"" + DateTimeFormatter.ofPattern(dateTimeFormat).format(startTime) + "\",\n" +
                "    \"end\": \"" + DateTimeFormatter.ofPattern(dateTimeFormat).format(endTime) + "\"\n" +
                "}";

        BookingDTO bookingDTO = json.parse(jsonBody).getObject();

        assertAll(
                () -> assertEquals(startTime, bookingDTO.getStart()),
                () -> assertEquals(endTime, bookingDTO.getEnd())
        );
    }
}