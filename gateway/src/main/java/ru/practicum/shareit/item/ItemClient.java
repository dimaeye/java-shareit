package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItemsByOwnerId(int userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", (long) userId, parameters);
    }

    public ResponseEntity<Object> addItem(int userId, ItemDTO itemDTO) {
        return post("", userId, itemDTO);
    }

    public ResponseEntity<Object> updateItem(int userId, int itemId, ItemDTO itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(int userId, int itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAvailableItemsByName(int userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", (long) userId, parameters);
    }

    public ResponseEntity<Object> createComment(int userId, int itemId, CommentDTO commentDTO) {
        return post("/" + itemId + "/comment", userId, commentDTO);
    }
}