package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


public class BookingMapper {
    public static BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .item(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static Booking bookingFromDto(BookingDto booking, Item item, User booker) {
        return Booking.builder()
                .id(booking.getId())
                .item(item)
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booker)
                .status(booking.getStatus() == null ? BookingStatus.WAITING : booking.getStatus())
                .build();
    }

    public static ItemBooking bookingToItemBooking(Booking booking) {
        return ItemBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
