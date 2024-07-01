package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRepoTest {

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void beforeEach() {
        User user1 = User.builder()
                .name("user1 name")
                .email("user1@mail.com")
                .build();
        User user2 = User.builder()
                .name("user2 name")
                .email("user2@mail.com")
                .build();
        Item item1 = Item.builder()
                .name("item1 name")
                .description("item1 description")
                .owner(user1)
                .available(true)
                .build();
        Item item2 = Item.builder()
                .name("item2 name")
                .description("item2 description")
                .owner(user1)
                .available(true)
                .build();

        Booking booking1 = Booking.builder()
                .item(item1)
                .start(CURRENT_TIME.plusHours(1))
                .end(CURRENT_TIME.plusHours(2))
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        Booking booking2 = Booking.builder()
                .item(item2)
                .start(CURRENT_TIME.plusHours(3))
                .end(CURRENT_TIME.plusHours(5))
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
    }

    @Test
    public void findAllByBookerIdOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByBookerIdOrderByStartDesc(2L, PAGEABLE);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getStart(), is(CURRENT_TIME.plusHours(3)));
    }

    @Test
    public void findAllByItemOwnerIdOrderByStartDescTest() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(1L, PAGEABLE);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getStart(), is(CURRENT_TIME.plusHours(3)));
    }

    @Test
    public void findAllByItemIdTest() {
        List<Booking> result = bookingRepository.findAllByItemId(1L);

        assertThat(result.size(), is(1));
    }

    @Test
    public void findFirstByItemIdAndStatusAndStartAfterOrderByStartTest() {
        Booking result = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(1L, BookingStatus.WAITING, CURRENT_TIME);

        assertThat(result.getId(), is(1L));
    }

    @Test
    public void findFirstBookingByEndBeforeAndItemIdAndBookerIdAndStatusTest() {
        Optional<Booking> result = bookingRepository.findFirstBookingByEndBeforeAndItemIdAndBookerIdAndStatus(CURRENT_TIME.plusHours(10), 1L, 2L, BookingStatus.WAITING);

        assertThat(result.isPresent(), is(true));
    }
}