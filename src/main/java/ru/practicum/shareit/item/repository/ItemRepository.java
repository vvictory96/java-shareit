package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long id, Pageable pageable);

    List<Item> findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String description, String name, Pageable pageable);

    List<Item> findAllByRequestId(long id);
}
