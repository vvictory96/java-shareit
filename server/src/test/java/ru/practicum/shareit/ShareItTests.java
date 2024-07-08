package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
class ShareItTests {

    @Autowired
    private BookingController bookingController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private ItemRequestController requestController;
    @Autowired
    private UserController userController;

    @Test
    void contextLoads() {
        assertThat(bookingController).isNotNull();
        assertThat(itemController).isNotNull();
        assertThat(requestController).isNotNull();
        assertThat(userController).isNotNull();
    }

}
