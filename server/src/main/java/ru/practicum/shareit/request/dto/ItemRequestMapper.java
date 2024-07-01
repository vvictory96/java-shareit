package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest request, List<ItemDtoRequest> items) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toRequest(ItemRequestDto request, User requestor, List<Item> items) {
        return ItemRequest.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requestor(requestor)
                .items(items)
                .build();
    }
}
