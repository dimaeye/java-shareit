package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.auth.AuthConstant.OWNER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDTO addRequest(
            @RequestHeader(OWNER_ID_HEADER) Integer requestorId,
            @RequestBody ItemRequestDTO itemRequestDTO
    ) {
        log.info("Запрос на добавление вещи: " + itemRequestDTO + " от пользователя: " + requestorId);
        ItemRequest addedItemRequest = itemRequestService.addRequest(
                ItemRequestMapper.toItemRequest(itemRequestDTO), requestorId
        );
        log.info("Запрос добавлен успешно");

        return ItemRequestMapper.toItemRequestDTO(addedItemRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDTO getRequest(
            @RequestHeader(OWNER_ID_HEADER) Integer requestorId, @PathVariable Integer requestId
    ) {
        return ItemRequestMapper.toItemRequestDTO(itemRequestService.getRequest(requestId, requestorId));
    }

    @GetMapping
    public List<ItemRequestDTO> getAllRequestsByRequestorId(
            @RequestHeader(OWNER_ID_HEADER) Integer requestorId
    ) {
        return itemRequestService.getAllRequestsByRequestorId(requestorId)
                .stream().map(ItemRequestMapper::toItemRequestDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDTO> getAllRequests(
            @RequestHeader(OWNER_ID_HEADER) Integer requestorId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return itemRequestService.getAllRequests(requestorId, from, size)
                .stream().map(ItemRequestMapper::toItemRequestDTO)
                .collect(Collectors.toList());
    }

}
