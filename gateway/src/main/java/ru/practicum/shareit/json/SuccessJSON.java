package ru.practicum.shareit.json;

import lombok.Data;

@Data
public class SuccessJSON {

    private final String success;

    public SuccessJSON(String success) {
        this.success = success;
    }
}

