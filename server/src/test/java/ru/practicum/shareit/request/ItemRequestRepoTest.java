package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepoTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository requestRepository;

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
        User user3 = User.builder()
                .name("user3 name")
                .email("user3@mail.com")
                .build();

        ItemRequest request1 = ItemRequest.builder()
                .created(Date.from(Instant.now()))
                .requestor(user1)
                .description("description1")
                .build();
        ItemRequest request2 = ItemRequest.builder()
                .created(Date.from(Instant.now()))
                .requestor(user2)
                .description("description2")
                .build();
        ItemRequest request3 = ItemRequest.builder()
                .created(Date.from(Instant.now()))
                .requestor(user1)
                .description("description3")
                .build();

        Item item1 = Item.builder()
                .name("item1 name")
                .description("item1 description")
                .owner(user3)
                .request(request1)
                .available(true)
                .build();
        Item item2 = Item.builder()
                .name("item2 name")
                .description("item2 description")
                .owner(user3)
                .request(request2)
                .available(true)
                .build();
        Item item3 = Item.builder()
                .name("item2 name")
                .description("item2 description")
                .owner(user3)
                .request(request3)
                .available(true)
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(request3);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
    }

    @Test
    public void findAllByRequestorIdTest() {
        List<ItemRequest> result = requestRepository.findAllByRequestorId(1L, PAGEABLE);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(1L));
        assertThat(result.get(1).getId(), is(3L));
    }

    @Test
    public void findAllByRequestorIdNotTest() {
        List<ItemRequest> result = requestRepository.findAllByRequestorIdNot(1L, PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(2L));
    }
}
