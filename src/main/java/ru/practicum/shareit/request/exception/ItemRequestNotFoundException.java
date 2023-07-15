package ru.practicum.shareit.request.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(int requestId) {
        super("Запрос с id=" + requestId + " не найден!");
    }
}
