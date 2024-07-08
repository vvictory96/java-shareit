package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.paging.Paging;
import ru.practicum.shareit.paging.PagingParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@PagingParam({0, 10}) Paging paging,
                                                     @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START FIND ALL USER'S REQUESTS");
        return requestClient.getAllUserRequests(paging, userId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                             @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START ADD REQUEST");
        return requestClient.addRequest(itemRequestDto, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@PagingParam({0, 10}) Paging paging,
                                                  @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START FIND ALL REQUESTS");
        return requestClient.findAllRequests(paging, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId,
                                                 @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START FIND REQUEST BY ID");
        return requestClient.getRequestById(requestId, userId);
    }
}
