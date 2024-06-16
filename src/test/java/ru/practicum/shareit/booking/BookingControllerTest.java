package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingCondition;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ArgumentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private BookingDto bookingResult;

    @BeforeEach
    void beforeEach() {
        LocalDateTime currentTime = LocalDateTime.now();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(currentTime.plusHours(1))
                .end(currentTime.plusHours(2))
                .build();

        User user1 = new User(1L, "user1 name", "user1@mail.com");
        User user2 = new User(2L, "user2 name", "user2@mail.com");
        Item item = Item.builder()
                .name("item1 name")
                .description("item1 description")
                .owner(user1)
                .available(true)
                .build();

        bookingResult = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .item(item)
                .start(currentTime.plusHours(1))
                .end(currentTime.plusHours(2))
                .booker(user2)
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();
    }


    @Test
    @SneakyThrows
    public void approveBookingTest() {
        BookingDto bookingResultApproved = bookingResult;
        bookingResultApproved.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(1L, true, 1L)).thenReturn(bookingResultApproved);

        checkBookingProps(mockMvc.perform(patch("/bookings/1")
                .param("approved", "True")
                .header(USER_ID_HEADER, 1L)));
    }

    @Test
    @SneakyThrows
    public void getBookingByIdTest() {
        when(bookingService.getBooking(1L, 1L)).thenReturn(bookingResult);

        checkBookingProps(mockMvc.perform(get("/bookings/1")
                .header(USER_ID_HEADER, 1L)));
    }

    @Test
    @SneakyThrows
    public void getAllByUser() {
        when(bookingService.getAllByUser(1L, BookingCondition.ALL, 0, 10))
                .thenReturn(List.of(bookingResult));

        checkBookingListProps(mockMvc.perform(get("/bookings")
                .header(USER_ID_HEADER, 1L)));
    }

    @Test
    @SneakyThrows
    public void getBookingsByItemsTest() {
        when(bookingService.getBookingsByItems(1L, BookingCondition.ALL, 0, 10))
                .thenReturn(List.of(bookingResult));

        checkBookingListProps(mockMvc.perform(get("/bookings/owner")
                .header(USER_ID_HEADER, 1L)));
    }

    @Test
    public void checkValid() {
        ArgumentException exception = assertThrows(ArgumentException.class, () ->
                bookingService.getAllByUser(1L, BookingCondition.convert("STATE"), 0, 10));

        assertThat(exception.getMessage(), is("Unknown state: STATE"));
    }

    private static void checkBookingProps(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.itemId").isNumber())
                .andExpect(jsonPath("$.item").isNotEmpty())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.bookerId").isNumber())
                .andExpect(jsonPath("$.status").isString())
                .andDo(print())
                .andReturn();
    }

    private static void checkBookingListProps(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].itemId").isNumber())
                .andExpect(jsonPath("$[0].item").isNotEmpty())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].booker").isNotEmpty())
                .andExpect(jsonPath("$[0].bookerId").isNumber())
                .andExpect(jsonPath("$[0].status").isString())
                .andDo(print())
                .andReturn();
    }
}
