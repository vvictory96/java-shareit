package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@Builder
@Entity
@Table(name = "requests", schema = "public")
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String description;

    @ManyToOne
    private User requestor;

    @OneToMany
    private List<Item> items;

    @Column(name = "creation_date")
    private Date created;
}
