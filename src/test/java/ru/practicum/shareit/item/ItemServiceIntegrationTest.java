package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class ItemServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService requestService;

    private UserDto user1;
    private UserDto user2;
    ItemDto item1Dto;
    BookingDto itemBooking1;
    BookingDto itemBooking2;
    CommentDto comment1;
    ItemRequestDto request;

    @BeforeEach
    void beforeEach() {
        LocalDateTime currentTime = LocalDateTime.now();

        user1 = UserDto.builder()
                .name("user1 name")
                .email("user1@mail.com")
                .build();
        user2 = UserDto.builder()
                .name("user2 name")
                .email("user2@mail.com")
                .build();
        item1Dto = ItemDto.builder()
                .name("item1 name")
                .description("item1 description")
                .requestId(1L)
                .available(true)
                .build();
        itemBooking1 = BookingDto.builder()
                .itemId(1L)
                .start(currentTime.minusDays(1))
                .end(currentTime.minusHours(1))
                .build();
        itemBooking2 = BookingDto.builder()
                .itemId(1L)
                .start(currentTime.plusDays(1))
                .end(currentTime.plusDays(1).plusHours(1))
                .build();
        comment1 = CommentDto.builder()
                .text("comment1 text")
                .build();
        request = ItemRequestDto.builder()
                .description("item1 request description")
                .build();
    }

    @Test
    public void getItemsList_WithElements_WithNextAndLastBooking() throws InterruptedException {
        userService.addUser(user1);
        userService.addUser(user2);

        requestService.addRequest(request, 2L);

        itemService.addItem(item1Dto, 1L);

        itemBooking1.setStart(LocalDateTime.now().plusSeconds(1));
        itemBooking1.setEnd(LocalDateTime.now().plusSeconds(2));
        itemBooking2.setStart(LocalDateTime.now().plusDays(1));
        itemBooking2.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.addBooking(itemBooking1, 2L);
        bookingService.addBooking(itemBooking2, 2L);
        bookingService.approveBooking(1L, true, 1L);
        bookingService.approveBooking(2L, true, 1L);

        Thread.sleep(3000);

        commentService.add(1L, comment1, 2L);

        List<ItemDto> result = itemService.getItemsList(1L, 0, 10);

        assertThat(result.size(), is(1));

        ItemDto result1 = result.get(0);

        assertThat(result1.getId(), is(1L));
        assertThat(result1.getName(), is("item1 name"));
        assertThat(result1.getDescription(), is("item1 description"));
        assertThat(result1.getAvailable(), is(true));
        assertThat(result1.getOwner().getId(), is(1L));
        assertThat(result1.getLastBooking().getId(), is(1L));
        assertThat(result1.getNextBooking().getId(), is(2L));
        assertThat(result1.getComments().get(0).getId(), is(1L));
    }
}
