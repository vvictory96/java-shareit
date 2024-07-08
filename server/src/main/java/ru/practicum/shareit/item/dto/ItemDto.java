package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private ItemBooking lastBooking;
    private ItemBooking nextBooking;
    private List<CommentDto> comments;
}
