package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectExistenceException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(ItemRequestDto requestDto, long userId) {
        validateUser(userId);

        requestDto.setCreated(Date.from(Instant.now()));
        ItemRequest itemRequest = ItemRequestMapper.toRequest(requestDto,
                userRepository.getReferenceById(userId),
                new ArrayList<>());

        return ItemRequestMapper.toDto(requestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId, int size, int from) {
        validateUser(userId);
        Pageable pageable = makePageable(from, size);

        return requestRepository.findAllByRequestorId(userId, pageable).stream()
                .map(o -> ItemRequestMapper.toDto(o, getItemsDto(o)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllRequests(int from, int size, long userId) {
        validateUser(userId);
        Pageable pageable = makePageable(from, size);

        return requestRepository.findAllByRequestorIdNot(userId, pageable).stream()
                .map(request -> {
                    List<ItemDtoRequest> items = getItemsDto(request);
                    return ItemRequestMapper.toDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(long id, long userId) {
        validateUser(userId);

        ItemRequest request = requestRepository.getReferenceById(id);

        return ItemRequestMapper.toDto(request, getItemsDto(request));
    }

    private List<ItemDtoRequest> getItemsDto(ItemRequest request) {
        return itemRepository.findAllByRequestId(request.getId()).stream()
                .map(ItemMapper::toItemForRequest)
                .collect(Collectors.toList());
    }

    private void validateUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectExistenceException(String.format("User with id: %d not found!", userId));
        }
    }

    private Pageable makePageable(int from, int size) {
        int page = from == 0 ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
