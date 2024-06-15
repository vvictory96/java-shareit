package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.booking.service.ItemBookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.ObjectUpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

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
public class ItemServiceUnitTest {

    @Mock
    private ItemBookingService bookingService;
    @Mock
    private CommentService commentService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item1;
    private ItemDto item1Dto;
    private ItemBooking itemBooking1;
    private ItemBooking itemBooking2;
    private CommentDto comment1;

    @BeforeEach
    void beforeEach() {
        LocalDateTime currentTime = LocalDateTime.now();

        User user1 = new User(1L, "user1 name", "user1@mail.com");
        User user2 = new User(2L, "user2 name", "user2@mail.com");
        item1 = Item.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .owner(user1)
                .available(true)
                .build();
        item1Dto = ItemDto.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .owner(user1)
                .available(true)
                .build();
        itemBooking1 = ItemBooking.builder()
                .id(1L)
                .start(currentTime.minusDays(1))
                .end(currentTime.minusHours(1))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .build();
        itemBooking2 = ItemBooking.builder()
                .id(2L)
                .start(currentTime.plusDays(1))
                .end(currentTime.plusDays(1).plusHours(1))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .build();
        comment1 = new CommentDto(1L, "comment1 text", user2.getName(), currentTime);
    }

    @Test
    public void getItem_ForUser_AndExpectLastBooking() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingService.getLastBooking(1L)).thenReturn(itemBooking1);
        when(bookingService.getNextBooking(1L)).thenReturn(itemBooking2);
        when(commentService.getAllCommentsByItemId(1L)).thenReturn(List.of(comment1));

        ItemDto result = itemService.getItem(1L, 1L);

        verify(itemRepository, times(1)).findById(1L);
        verify(bookingService, times(1)).getLastBooking(1L);
        verify(bookingService, times(1)).getNextBooking(1L);
        verify(commentService, times(2)).getAllCommentsByItemId(1L);
        assertItem(item1, result);
    }

    @Test
    public void updateItem_AndExpectError() {
        ItemDto updatedItem = item1Dto;
        updatedItem.setOwner(new User());
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        ObjectUpdateException exception = assertThrows(ObjectUpdateException.class, () ->
                itemService.updateItem(1L, updatedItem, 1L));

        assertThat(exception.getMessage(), is("These fields can't be updated"));
    }

    @Test
    public void searchItem_WithBlankText_AndExpectEmptyList() {
        List<ItemDto> result = itemService.searchItem("", 0, 10);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void searchItemTest() {
        when(itemRepository.findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(
                "item1", "item1", PageRequest.of(0, 10)))
                .thenReturn(List.of(item1));

        List<ItemDto> result = itemService.searchItem("item1", 0, 10);

        assertThat(result.size(), is(1));
        assertItem(item1, result.get(0));
    }

    private void assertItem(Item input, ItemDto output) {
        assertThat(input.getId(), is(output.getId()));
        assertThat(input.getName(), is(output.getName()));
        assertThat(input.getDescription(), is(output.getDescription()));
        assertThat(input.isAvailable(), is(output.getAvailable()));
        assertThat(input.getOwner(), is(output.getOwner()));
    }
}
