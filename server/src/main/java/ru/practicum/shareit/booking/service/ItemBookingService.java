package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.ItemBooking;

public interface ItemBookingService {
    ItemBooking getLastBooking(long itemId);

    ItemBooking getNextBooking(long itemId);

    boolean checkBookingCompleted(long itemId, long userId);
}
