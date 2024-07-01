package ru.practicum.shareit.booking;

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
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingDto booking,
                                 @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START CREATE BOOKING ENDPOINT---");
        return bookingService.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START UPDATE BOOKING ENDPOINT---");
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START FIND BOOKING ENDPOINT---");
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestParam BookingCondition state,
                                         @RequestHeader(value = USER_ID_HEADER) Long userId,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "0") int from) {
        log.info("---START FIND ALL BOOKING ENDPOINT---");
        return bookingService.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItems(@RequestParam(defaultValue = "ALL") BookingCondition state,
                                               @RequestHeader(value = USER_ID_HEADER) long userId,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "0") int from) {
        log.info("---START FIND BOOKING BY ITEM ENDPOINT---");
        return bookingService.getBookingsByItems(userId, state, from, size);
    }
}
