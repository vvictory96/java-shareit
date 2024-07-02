package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingCondition;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingDto booking,
                                 @RequestHeader(value = USER_ID_HEADER) long userId) {
        return service.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(value = USER_ID_HEADER) long userId) {
        return service.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(value = USER_ID_HEADER) long userId) {
        return service.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestParam String state,
                                         @RequestHeader(value = USER_ID_HEADER) Long userId,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "0") int from) {
        return service.getAllByUser(userId, BookingCondition.convert(state), from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItems(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(value = USER_ID_HEADER) long userId,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "0") int from) {
        return service.getBookingsByItems(userId, BookingCondition.convert(state), from, size);
    }
}
