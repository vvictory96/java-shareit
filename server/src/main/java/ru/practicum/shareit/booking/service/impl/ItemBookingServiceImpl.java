package ru.practicum.shareit.booking.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.ItemBookingService;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class ItemBookingServiceImpl implements ItemBookingService {
    private final BookingRepository repository;

    @Override
    public ItemBooking getLastBooking(long itemId) {

        final LocalDateTime currentTime = LocalDateTime.now();
        final Booking lastBooking = repository.findAllByItemId(itemId).stream()
                .filter(o -> o.getStart().isBefore(currentTime) || o.getStart().isEqual(currentTime))
                .max(Comparator.comparing(Booking::getEnd)).orElse(null);

        return lastBooking == null ? null : BookingMapper.bookingToItemBooking(lastBooking);
    }

    @Override
    public ItemBooking getNextBooking(long itemId) {
        try {
            final LocalDateTime currentTime = LocalDateTime.now();
            final Booking nextBooking = repository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(itemId,
                    BookingStatus.APPROVED,
                    currentTime);

            return BookingMapper.bookingToItemBooking(nextBooking);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public boolean checkBookingCompleted(long itemId, long userId) {
        final LocalDateTime CURRENT_TIME = LocalDateTime.now();
        return repository.findFirstBookingByEndBeforeAndItemIdAndBookerIdAndStatus(CURRENT_TIME,
                itemId, userId, BookingStatus.APPROVED).isEmpty();
    }
}
