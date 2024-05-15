package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectAccessException;
import ru.practicum.shareit.exception.ObjectUpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items;
    private final UserService userService;
    private long id = 1;

    @Override
    public ItemDto addItem(ItemDto item, Long idUser) {
        log.info(format("Create item: %s", item));
        item.setOwner(UserMapper.toUser(userService.getUser(idUser)));
        item.setId(id++);
        items.put(item.getId(), ItemMapper.toItem(item));
        return item;
    }

    @Override
    public ItemDto getItem(Long id) {
        log.info(format("Get item by id: %s", id));
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public List<ItemDto> getItemsList(Long idUser) {
        return items.values().stream()
                .filter(o -> o.getOwner().getId() == idUser)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto item, Long idUser) {
        log.info(format("Start update item = [%s]", id));
        checkItemOnUpdate(id, item, idUser);

        Item itemToUpdate = items.get(id);

        itemToUpdate.setName(item.getName() == null ? itemToUpdate.getName() : item.getName());
        itemToUpdate.setDescription(item.getDescription() == null ? itemToUpdate.getDescription() : item.getDescription());
        itemToUpdate.setAvailable(item.getAvailable() == null ? itemToUpdate.isAvailable() : item.getAvailable());

        items.put(id, itemToUpdate);
        log.info("Item after update: {}", ItemMapper.toItemDto(itemToUpdate));
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Поиск: {}", text);
        return items.values().stream()
                .filter(o -> (o.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        o.getName().toLowerCase().contains(text.toLowerCase())) &&
                        o.isAvailable() &&
                        !text.isBlank())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItemOwner(Item item, Long userId) {
        if (item.getOwner().getId() != userId) {
            throw new ObjectAccessException("You can edit only your items");
        }
    }

    private void checkItemOnUpdate(Long id, ItemDto item, Long userId) {
        checkItemOwner(items.get(id), userId);

        if (item.getOwner() != null || item.getRequest() != null)
            throw new ObjectUpdateException("These fields can't be updated");
    }
}
