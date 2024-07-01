package ru.practicum.shareit.paging;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Paging {

    private int size;
    private int from;
}
