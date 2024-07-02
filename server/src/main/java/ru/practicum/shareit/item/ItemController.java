package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService service;
    private final CommentService commentService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId,
                           @RequestHeader(USER_ID_HEADER) long userId) {
        return service.getItem(itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto item,
                           @RequestHeader(value = USER_ID_HEADER) Long userId) {
        return service.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemDto item,
                              @RequestHeader(value = USER_ID_HEADER) Long userId) {
        return service.updateItem(itemId, item, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsList(@RequestHeader(value = USER_ID_HEADER) Long userId,
                                      @RequestParam(defaultValue = "10")int size,
                                      @RequestParam(defaultValue = "0")int from) {
        return service.getItemsList(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "10")int size,
                                     @RequestParam(defaultValue = "0")int from) {
        return service.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestBody CommentDto comment,
                                 @RequestHeader(value = USER_ID_HEADER) Long userId) {
        return commentService.add(itemId, comment, userId);
    }
}
