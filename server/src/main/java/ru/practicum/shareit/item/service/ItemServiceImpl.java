package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.ItemBookingService;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.ObjectAccessException;
import ru.practicum.shareit.exception.ObjectExistenceException;
import ru.practicum.shareit.exception.ObjectUpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemBookingService bookingService;
    private final CommentService commentService;
    private final ItemRepository repository;
    private final ItemRequestRepository requestRepository;


    @Override
    @Transactional
    public ItemDto addItem(ItemDto item, Long userId) {
        item.setOwner(UserMapper.toUser(userService.getUser(userId)));
        ItemRequest request = item.getRequestId() == null ? null : requestRepository.getReferenceById(item.getRequestId());

        return ItemMapper.toItemDto(repository.save(ItemMapper.toItem(item, request)));
    }

    @Override
    public ItemDto getItem(Long id, long userId) {
        log.info(format("Get item by id: %s and user: %s ", id, userId));
        ItemDto item = ItemMapper.toItemDto(getItemById(id));
        item.setLastBooking(item.getOwner().getId() == userId ? bookingService.getLastBooking(item.getId()) : null);
        item.setNextBooking(item.getOwner().getId() == userId ? bookingService.getNextBooking(item.getId()) : null);
        item.setComments(commentService.getAllCommentsByItemId(id) == null ? new ArrayList<>() : commentService.getAllCommentsByItemId(id));
        return item;
    }

    @Override
    public List<ItemDto> getItemsList(Long userId, int from, int size) {
        return repository.findAllByOwnerId(userId, createPageable(from, size)).stream()
                .map(o -> {
                    o.setLastBooking(bookingService.getLastBooking(o.getId()));
                    o.setNextBooking(bookingService.getNextBooking(o.getId()));
                    o.setComments(commentService.getAllCommentsByItemId(o.getId()));
                    return ItemMapper.toItemDto(o);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long id, ItemDto item, Long idUser) {
        log.info(format("Start update item = [%s]", id));
        Item itemToUpdate = getItemById(id);
        checkItemOnUpdate(itemToUpdate, item, idUser);

        itemToUpdate.setName(item.getName() == null ? itemToUpdate.getName() : item.getName());
        itemToUpdate.setDescription(item.getDescription() == null ? itemToUpdate.getDescription() : item.getDescription());
        itemToUpdate.setAvailable(item.getAvailable() == null ? itemToUpdate.isAvailable() : item.getAvailable());

        repository.save(itemToUpdate);
        log.info("Item after update: {}", ItemMapper.toItemDto(itemToUpdate));
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        log.info("Поиск: {}, откуда: {}, размер: {}", text, from, size);
        if (text.isBlank())
            return new ArrayList<>();

        return repository.findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(text, text, createPageable(from, size))
                .stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItemOwner(Item item, Long userId) {
        if (item.getOwner().getId() != userId) {
            throw new ObjectAccessException("You can edit only your items");
        }
    }

    private void checkItemOnUpdate(Item itemToUpdate, ItemDto item, Long userId) {
        checkItemOwner(itemToUpdate, userId);

        if (item.getOwner() != null || item.getRequestId() != null)
            throw new ObjectUpdateException("These fields can't be updated");
    }

    private Item getItemById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectExistenceException("Item doesn't exists"));
    }

    private Pageable createPageable(int from, int size) {
        int page = from == 0 ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
