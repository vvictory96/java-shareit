package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceUnitTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private Item item;
    private ItemRequest request;

    @BeforeEach
    void beforeEach() {
        Date currentDate = Date.from(Instant.now());

        User owner = User.builder()
                .id(1)
                .name("user1 name")
                .email("user1@testmail.com")
                .build();
        User requestor = User.builder()
                .id(2)
                .name("user2 name")
                .email("user2@testmail.com")
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .requestor(requestor)
                .created(currentDate)
                .build();
        item = Item.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .owner(owner)
                .available(true)
                .request(request)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
    }

    @Test
    public void getUserRequestsTest() {
        when(requestRepository.findAllByRequestorId(2L, PageRequest.of(0, 10)))
                .thenReturn(List.of(request));

        List<ItemRequestDto> result = requestService.getUserRequests(2L, 10, 0);

        assertThat(result.size(), is(1));
        assertRequest(request, result.get(0));
    }

    @Test
    public void findAllRequestsTest() {
        when(requestRepository.findAllByRequestorIdNot(1L, PageRequest.of(0, 10)))
                .thenReturn(List.of(request));

        List<ItemRequestDto> result = requestService.findAllRequests(0, 10, 1L);

        assertThat(result.size(), is(1));
        assertRequest(request, result.get(0));
    }

    @Test
    public void getRequestByIdTest() {
        when(requestRepository.getReferenceById(1L)).thenReturn(request);

        ItemRequestDto result = requestService.getRequestById(1L, 2L);

        assertRequest(request, result);
    }

    private void assertRequest(ItemRequest exp, ItemRequestDto res) {
        assertThat(exp.getId(), is(res.getId()));
        assertThat(exp.getDescription(), is(res.getDescription()));
        assertThat(List.of(ItemMapper.toItemForRequest(item)), is(res.getItems()));
        assertThat(exp.getCreated(), is(res.getCreated()));
    }
}
