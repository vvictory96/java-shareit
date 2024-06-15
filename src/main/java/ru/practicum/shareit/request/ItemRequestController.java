package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.paging.Paging;
import ru.practicum.shareit.paging.PagingParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@PagingParam({0, 10}) Paging paging,
                                                   @RequestHeader(USER_ID_HEADER) long userId) {
        return requestService.getUserRequests(userId, paging.getSize(), paging.getFrom());
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                     @RequestHeader(USER_ID_HEADER) long userId) {
        return requestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(@PagingParam({0, 10}) Paging paging,
                                                @RequestHeader(USER_ID_HEADER) long userId) {
        return requestService.findAllRequests(paging.getFrom(), paging.getSize(), userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader(USER_ID_HEADER) long userId) {
        return requestService.getRequestById(requestId, userId);
    }
}
