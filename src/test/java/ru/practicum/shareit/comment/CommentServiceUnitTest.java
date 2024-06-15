package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.ItemBookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.exception.ObjectAvailabilityException;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {

    @Mock
    private ItemBookingService bookingService;
    @InjectMocks
    private CommentServiceImpl commentService;

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
}
