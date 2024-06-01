package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingCondition;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectAccessException;
import ru.practicum.shareit.exception.ObjectAvailabilityException;
import ru.practicum.shareit.exception.ObjectCreationException;
import ru.practicum.shareit.exception.ObjectExistenceException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto addBooking(BookingDto bookingDto, long userId) {
        checkDate(bookingDto);
        Item item = getItemById(bookingDto.getItemId());

        if (!item.isAvailable()) {
            throw new ObjectAvailabilityException("Item is not available");
        }
        User user = getUserById(userId);
        if (item.getOwner().equals(user)) {
            throw new ObjectAccessException("You can't booking your item");
        }

        return BookingMapper.bookingToDto(repository.save(BookingMapper.bookingFromDto(bookingDto, item, user)));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, boolean status, long userId) {
        BookingDto booking = BookingMapper.bookingToDto(getBookingById(bookingId));
        Item item = getItemById(booking.getItemId());

        if (item.getOwner().getId() != userId)
            throw new ObjectAccessException("You don't have access to this booking");
        if (booking.getStatus().equals(BookingStatus.APPROVED) || booking.getStatus().equals(BookingStatus.REJECTED))
            throw new ObjectAvailabilityException("Booking already " + booking.getStatus());

        booking.setStatus(status ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.bookingToDto(repository.save(BookingMapper.bookingFromDto(booking, item, booking.getBooker())));
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = getBookingById(bookingId);
        User user = getUserById(userId);

        if (booking.getItem().getOwner().getId() != user.getId() && booking.getBooker().getId() != user.getId())
            throw new ObjectAccessException("You don't have access to this booking");

        return BookingMapper.bookingToDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(long userId, BookingCondition status) {
        User user = getUserById(userId);

        return repository.findAllByBookerId(user.getId()).stream()
                .filter(o -> filterByCondition(o, status))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByItems(long userId, BookingCondition status) {
        User user = getUserById(userId);

        return repository.findAllByItemOwnerId(user.getId()).stream()
                .filter(o -> filterByCondition(o, status))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    private boolean filterByCondition(Booking booking, BookingCondition cond) {
        final LocalDateTime currentTime = LocalDateTime.now();

        switch (cond) {
            case REJECTED:
                return booking.getStatus().equals(BookingStatus.REJECTED);
            case PAST:
                return booking.getEnd().isBefore(currentTime);
            case CURRENT:
                return booking.getStart().isBefore(currentTime) &&
                        booking.getEnd().isAfter(currentTime);
            case FUTURE:
                return booking.getStart().isAfter(currentTime) ||
                        booking.getStart().equals(currentTime);
            case WAITING:
                return booking.getStatus().equals(BookingStatus.WAITING);
            default:
                return true;
        }
    }

    private Booking getBookingById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ObjectExistenceException("Booking doesn't exists"));
    }

    private User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectExistenceException("User doesn't exists"));
    }

    private Item getItemById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ObjectExistenceException("Item is not existing"));
    }

    private void checkDate(BookingDto booking) {
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().equals(booking.getEnd()))
            throw new ObjectCreationException("End date cannot be after/equal start date");
    }
}
