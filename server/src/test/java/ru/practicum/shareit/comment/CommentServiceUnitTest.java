package ru.practicum.shareit.comment;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.ItemBookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.exception.ObjectAvailabilityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {

    @Mock
    private ItemBookingService bookingService;
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;

    private CommentDto comment;

    @BeforeEach
    void beforeEach() {
        LocalDateTime currentTime = LocalDateTime.now();

        comment = CommentDto.builder()
                .id(1L)
                .text("sometext")
                .created(currentTime)
                .build();
    }

    @Test
    public void addComment_AndExpectError() {
        when(bookingService.checkBookingCompleted(1L, 1L)).thenReturn(true);

        ObjectAvailabilityException exception = assertThrows(ObjectAvailabilityException.class, () ->
                commentService.add(1L, comment, 1L));

        assertThat(exception.getMessage(), is("You don't book this item yet"));
    }

    @Test
    public void addComment() {
        User user = User.builder()
                .name("user1 name")
                .email("user1@mail.com")
                .build();
        Item item = Item.builder()
                .name("item1 name")
                .description("item1 description")
                .owner(user)
                .available(true)
                .build();

        when(bookingService.checkBookingCompleted(2L, 3L)).thenReturn(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Comment newCom = new Comment(1L, item, user, "sometext", comment.getCreated());
        when(commentRepository.save(any())).thenReturn(newCom);

        CommentDto commentDto = commentService.add(2L, comment, 3L);

        assertEquals(CommentMapper.toDto(newCom), commentDto);
    }
}
