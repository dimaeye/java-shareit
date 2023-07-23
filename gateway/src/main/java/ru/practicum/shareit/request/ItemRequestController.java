package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.user.auth.AuthConstant.OWNER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer requestorId,
            @RequestBody @Valid ItemRequestDTO itemRequestDTO
    ) {
        log.info("Запрос на добавление вещи: " + itemRequestDTO + " от пользователя: " + requestorId);
        return itemRequestClient.addRequest(requestorId, itemRequestDTO);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer requestorId, @PathVariable Integer requestId
    ) {
        return itemRequestClient.getRequest(requestorId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByRequestorId(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer requestorId
    ) {
        return itemRequestClient.getAllRequestsByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(OWNER_ID_HEADER) @Positive Integer requestorId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @Positive Integer size
    ) {
        return itemRequestClient.getAllRequests(requestorId, from, size);
    }

}
