package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.paging.Paging;

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

    public ResponseEntity<Object> getItem(Long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> addItem(ItemDto item, long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long itemId, ItemDto item, long userId) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> getItemsList(long userId, Paging paging) {
        Map<String, Object> parameters = Map.of(
                "from", paging.getFrom(),
                "size", paging.getSize()
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> searchItem(String text, Long userId, Paging paging) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", paging.getFrom(),
                "size", paging.getSize()
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long itemId, CommentDto comment, long userId) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
