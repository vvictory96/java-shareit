package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(ItemRequestDto requestDto, long userId);

    List<ItemRequestDto> getUserRequests(long userId, int size, int from);

    List<ItemRequestDto> findAllRequests(int from, int size, long userId);

    ItemRequestDto getRequestById(long id, long userId);
}
