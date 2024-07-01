package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<List<ItemRequestDto>> getAllUserRequests(@RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "0") int from,
                                                                   @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START FIND ALL USERS REQUESTS---");
        return new ResponseEntity<>(requestService.getUserRequests(userId, size, from), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> addRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                     @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START ADD REQUEST---");
        return new ResponseEntity<>(requestService.addRequest(itemRequestDto, userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> findAllRequests(@RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "0") int from,
                                                                @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START FIND ALL REQUESTS---");
        return new ResponseEntity<>(requestService.findAllRequests(from, size, userId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@PathVariable long requestId,
                                                         @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("---START FIND REQUEST BY ID---");
        return new ResponseEntity<>(requestService.getRequestById(requestId, userId), HttpStatus.OK);
    }
}
