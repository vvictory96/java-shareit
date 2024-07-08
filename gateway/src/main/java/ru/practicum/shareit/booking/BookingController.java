package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.paging.Paging;
import ru.practicum.shareit.paging.PagingParam;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Validated BookingDto booking,
                                             @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START CREATE BOOKING ENDPOINT---");
        log.info("Creating booking {}, userId={}", booking, userId);
        return bookingClient.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
                                                 @RequestParam boolean approved,
                                                 @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START UPDATE BOOKING ENDPOINT---");
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("---START FIND BOOKING ENDPOINT---");
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader(USER_ID_HEADER) long userId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state,
                                                    @PagingParam({0, 10}) Paging paging) {
        log.info("---START FIND ALL BOOKING ENDPOINT---");
        return bookingClient.getBookingsByUser(userId, state, paging.getFrom(), paging.getSize());
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByItems(@RequestParam(defaultValue = "ALL") BookingState state,
                                                    @RequestHeader(value = USER_ID_HEADER) long userId,
                                                    @PagingParam({0, 10}) Paging paging) {
        log.info("---START FIND BOOKING BY ITEM ENDPOINT---");
        return bookingClient.getBookingByItems(state, userId, paging.getSize(), paging.getFrom());
    }
}