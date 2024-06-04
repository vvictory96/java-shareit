package ru.practicum.shareit.request.model;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "requests", schema = "public")
@RequiredArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String description;

    @ManyToOne
    private User requestor;

    @Column(name = "creation_date")
    private Date created;
}
