package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRepoTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

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
                .owner(user2)
                .available(true)
                .build();

        User requestor = User.builder()
                .name("user3 name")
                .email("user3@mail.com")
                .build();
        ItemRequest request = ItemRequest.builder()
                .created(Date.from(Instant.now()))
                .requestor(requestor)
                .description("description")
                .build();
        Item item3 = Item.builder()
                .name("item3 name")
                .description("item3 description")
                .owner(user1)
                .request(request)
                .available(true)
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(requestor);
        entityManager.persist(request);
        entityManager.persist(item3);
    }

    @Test
    public void findAllByOwnerIdTest() {
        List<Item> result = itemRepository.findAllByOwnerId(1L, PAGEABLE);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(1L));
    }

    @Test
    public void findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase() {
        List<Item> result = itemRepository.findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(
                "item2", "somename", PAGEABLE
        );

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(2L));
    }

    @Test
    public void findAllByRequestIdTest() {
        List<Item> result = itemRepository.findAllByRequestId(1L);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(3L));
    }
}
