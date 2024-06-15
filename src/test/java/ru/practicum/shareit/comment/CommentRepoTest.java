package ru.practicum.shareit.comment;

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
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentRepoTest {

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

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

        Booking booking1 = Booking.builder()
                .item(item1)
                .start(CURRENT_TIME.minusHours(2))
                .end(CURRENT_TIME.minusHours(1))
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();

        Comment comment = Comment.builder()
                .text("comment text")
                .created(CURRENT_TIME)
                .user(user2)
                .item(item1)
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(booking1);
        entityManager.persist(comment);
    }

    @Test
    public void findCommentsByItem_IdTest() {
        List<Comment> result = commentRepository.findCommentsByItem_Id(1L);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(1L));
    }
}
