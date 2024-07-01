package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.RequestWithJson.postJson;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService requestService;

    private ItemRequestDto request;
    private ItemRequestDto requestResult;

    @BeforeEach
    void beforeEach() {
        Date currentDate = Date.from(Instant.now());

        request = ItemRequestDto.builder()
                .description("request description")
                .build();

        requestResult = ItemRequestDto.builder()
                .id(1L)
                .description("request description")
                .created(currentDate)
                .build();
    }

    @Test
    public void getAllUserRequestsTest() throws Exception {
        when(requestService.getUserRequests(1L, 10, 0))
                .thenReturn(List.of(requestResult));

        checkItemListProp(mockMvc.perform(get("/requests").header(USER_ID_HEADER, 1L)));
    }

    @Test
    public void addRequestTest() throws Exception {
        when(requestService.addRequest(request, 1L)).thenReturn(requestResult);

        checkItemProp(mockMvc.perform(postJson("/requests", request).header(USER_ID_HEADER, 1L)));
    }

    @Test
    public void findAllRequestsTest() throws Exception {
        when(requestService.findAllRequests(0, 10, 1L)).thenReturn(List.of(requestResult));

        checkItemListProp(mockMvc.perform(get("/requests/all").header(USER_ID_HEADER, 1L)));
    }

    @Test
    public void getRequestById() throws Exception {
        when(requestService.getRequestById(1L, 1L)).thenReturn(requestResult);

        checkItemProp(mockMvc.perform(get("/requests/1").header(USER_ID_HEADER, 1L)));
    }

    private static void checkItemProp(ResultActions request) throws Exception {
        request.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andReturn();
    }

    private static void checkItemListProp(ResultActions request) throws Exception {
        request.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].description").isString())
                .andExpect(jsonPath("$[0].created").isNotEmpty());
    }
}
