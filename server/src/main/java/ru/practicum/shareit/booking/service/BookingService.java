package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingCondition;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto booking, long userId);

    BookingDto approveBooking(long bookingId, boolean status, long userId);

    BookingDto getBooking(long bookingId, long userId);

    List<BookingDto> getAllByUser(long userId, BookingCondition status, int from, int size);

    List<BookingDto> getBookingsByItems(long userId, BookingCondition status, int from, int size);
}
