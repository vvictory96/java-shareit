package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.paging.Paging;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllUserRequests(Paging paging, long userId) {
        Map<String, Object> parameters = Map.of(
                "from", paging.getFrom(),
                "size", paging.getSize()
        );

        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addRequest(ItemRequestDto request, long userId) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> findAllRequests(Paging paging, long userId) {
        Map<String, Object> parameters = Map.of(
                "from", paging.getFrom(),
                "size", paging.getSize()
        );

        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequestById(long requestId, long userId) {
        return get("/" + requestId, userId);
    }
}
