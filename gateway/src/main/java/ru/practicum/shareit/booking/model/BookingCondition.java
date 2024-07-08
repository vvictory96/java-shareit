package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ArgumentException;

public enum BookingCondition {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingCondition convert(String source) {
        try {
            return BookingCondition.valueOf(source);
        } catch (Exception e) {
            String message = String.format("Unknown state: %S", source);
            throw new ArgumentException(message);
        }
    }
}
