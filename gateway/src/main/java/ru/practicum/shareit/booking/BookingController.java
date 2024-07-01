package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.booking.model.BookingCondition;
import ru.practicum.shareit.paging.Paging;
import ru.practicum.shareit.paging.PagingParam;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingDto booking,
                                             @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START CREATE BOOKING ENDPOINT---");
        log.info("Creating booking {}, userId={}", booking, userId);
        return new ResponseEntity<>(bookingClient.addBooking(booking, userId), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
                                                 @RequestParam boolean approved,
                                                 @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START UPDATE BOOKING ENDPOINT---");
        return new ResponseEntity<>(bookingClient.approveBooking(bookingId, approved, userId), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
                                                 @RequestHeader(value = USER_ID_HEADER) long userId) {
        log.info("---START FIND BOOKING ENDPOINT---");
        log.info("Get booking {}, userId={}", bookingId, userId);
        return new ResponseEntity<>(bookingClient.getBooking(bookingId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(value = USER_ID_HEADER) Long userId,
                                               @PagingParam({0, 10}) Paging paging) {
        log.info("---START FIND ALL BOOKING ENDPOINT---");
        return new ResponseEntity<>(bookingClient.getAllBookingsByUser(userId, BookingCondition.convert(state), paging.getFrom(), paging.getSize()), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByItems(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader(value = USER_ID_HEADER) long userId,
                                                     @PagingParam({0, 10}) Paging paging) {
        log.info("---START FIND BOOKING BY ITEM ENDPOINT---");
        return new ResponseEntity<>(bookingClient.getAllBookingsByItems(BookingCondition.convert(state), userId, paging.getFrom(), paging.getSize()), HttpStatus.OK);
    }

}
