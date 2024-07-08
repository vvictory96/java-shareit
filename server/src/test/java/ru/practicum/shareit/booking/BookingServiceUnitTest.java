package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingCondition;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectAccessException;
import ru.practicum.shareit.exception.ObjectAvailabilityException;
import ru.practicum.shareit.exception.ObjectCreationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private User user;
    private User user2;
    private Item item;

    @BeforeEach
    void beforeEach() {
        LocalDateTime currentTime = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .name("user1 name")
                .email("user1@testmail.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2 name")
                .email("user2@testmail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item name")
                .description("description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(currentTime.plusHours(1))
                .end(currentTime.plusHours(2))
                .booker(user)
                .build();
    }

    @Test
    public void addBooking_AndExpectError() {
        booking.setEnd(LocalDateTime.MIN);

        ObjectCreationException exception = assertThrows(ObjectCreationException.class, () ->
                bookingService.addBooking(BookingMapper.bookingToDto(booking), 1L));

        assertThat(exception.getMessage(), is("End date cannot be before/equal start date"));
    }

    @Test
    public void addBooking_AndExpectAvailabilityException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        item.setAvailable(false);

        ObjectAvailabilityException exception = assertThrows(ObjectAvailabilityException.class, () ->
                bookingService.addBooking(BookingMapper.bookingToDto(booking), 1L));

        assertThat(exception.getMessage(), is("Item is not available"));
    }

    @Test
    public void addBooking_AndExpectAccessException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ObjectAccessException exception = assertThrows(ObjectAccessException.class, () ->
                bookingService.addBooking(BookingMapper.bookingToDto(booking), 1L));

        assertThat(exception.getMessage(), is("You can't booking your item"));
    }

    @Test
    public void approveBooking_AndExpectAccessError() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ObjectAccessException exception = assertThrows(ObjectAccessException.class, () ->
                bookingService.approveBooking(1L, true, 2L));
        assertThat(exception.getMessage(), is("You don't have access to this booking"));
    }

    @Test
    public void approveBooking_AndExpectAvailabilityException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        booking.setStatus(BookingStatus.APPROVED);

        ObjectAvailabilityException exception = assertThrows(ObjectAvailabilityException.class, () ->
                bookingService.approveBooking(1L, true, 1L));
        assertThat(exception.getMessage(), is("Booking already APPROVED"));

    }

    @Test
    public void getBookingTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(1L, 1L);

        verify(bookingRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);

        assertBooking(booking, result);
    }

    @Test
    public void getBooking_AndExpectError() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        ObjectAccessException exception = assertThrows(ObjectAccessException.class, () ->
                bookingService.getBooking(1L, 2L));

        assertThat(exception.getMessage(), is("You don't have access to this booking"));
    }

    @Test
    public void getAllByUserTest() {
        mockUserAndBooking();

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingCondition.ALL, 0, 10);

        assertThat(result.size(), is(1));
        assertBooking(booking, result.get(0));
    }

    @Test
    public void getAllByUser_WithFilterRejected() {
        mockUserAndBooking();

        booking.setStatus(BookingStatus.REJECTED);

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingCondition.REJECTED, 0, 10);

        assertThat(result.size(), is(1));
        assertBooking(booking, result.get(0));
    }

    @Test
    public void getAllByUser_WithFilterPast() {
        mockUserAndBooking();

        LocalDateTime currentTime = LocalDateTime.now();
        booking.setStart(currentTime.minusDays(1));
        booking.setEnd(currentTime.minusHours(1));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingCondition.PAST, 0, 10);

        assertThat(result.size(), is(1));
        assertBooking(booking, result.get(0));
    }

    @Test
    public void getAllByUser_WithFilterCurrent() {
        mockUserAndBooking();

        LocalDateTime currentTime = LocalDateTime.now();
        booking.setStart(currentTime.minusDays(1));
        booking.setEnd(currentTime.plusDays(1));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingCondition.CURRENT, 0, 10);

        assertThat(result.size(), is(1));
        assertBooking(booking, result.get(0));
    }

    @Test
    public void getAllByUser_WithFilterFuture() {
        mockUserAndBooking();

        LocalDateTime currentTime = LocalDateTime.now();
        booking.setStart(currentTime.plusDays(1));
        booking.setEnd(currentTime.plusDays(2));

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingCondition.FUTURE, 0, 10);

        assertThat(result.size(), is(1));
        assertBooking(booking, result.get(0));
    }

    @Test
    public void getAllByUser_WithFilterWaiting() {
        mockUserAndBooking();

        booking.setStatus(BookingStatus.WAITING);

        List<BookingDto> result = bookingService.getAllByUser(1L, BookingCondition.WAITING, 0, 10);

        assertThat(result.size(), is(1));
        assertBooking(booking, result.get(0));
    }

    @Test
    public void getBookingsByItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsByItems(1L, BookingCondition.ALL, 0, 10);

        assertThat(result.size(), is(1));
        assertBooking(booking, result.get(0));
    }

    private void assertBooking(Booking expected, BookingDto result) {
        assertThat(expected.getId(), is(result.getId()));
        assertThat(expected.getItem().getId(), is(result.getItemId()));
        assertThat(expected.getStart(), is(result.getStart()));
        assertThat(expected.getEnd(), is(result.getEnd()));
        assertThat(expected.getBooker().getId(), is(result.getBookerId()));
    }

    private void mockUserAndBooking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(List.of(booking));
    }
}
