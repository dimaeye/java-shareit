package ru.practicum.shareit.item.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(int itemId) {
        super("Вещь с id=" + itemId + " не найдена!");
    }

    public ItemNotFoundException() {
        super("Вещь не найдена!");
    }
}
