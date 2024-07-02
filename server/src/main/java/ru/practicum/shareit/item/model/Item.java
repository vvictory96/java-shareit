package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@Table(name = "items", schema = "public")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private boolean available;

    @ManyToOne
    private User owner;

    @ManyToOne
    private ItemRequest request;

    @Transient
    private ItemBooking lastBooking;

    @Transient
    private ItemBooking nextBooking;

    @Transient
    private List<CommentDto> comments;
}
