package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.RequestWithJson.patchJson;
import static ru.practicum.shareit.RequestWithJson.postJson;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    private ItemDto item1;

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1L, "user1 name", "user1@mail.com");

        item1 = ItemDto.builder()
                .name("item1 name")
                .description("item1 description")
                .owner(user1)
                .available(true)
                .build();
    }

    @Test
    public void getItemTest() throws Exception {
        when(itemService.getItem(1L, 1L)).thenReturn(item1);

        checkItemProp(mockMvc.perform(get("/items/1")
                .header(USER_ID_HEADER, 1)));
    }

    @Test
    public void addItemTest() throws Exception {
        when(itemService.addItem(item1, 1L)).thenReturn(item1);

        checkItemProp(mockMvc.perform(postJson("/items", item1)
                .header(USER_ID_HEADER, 1)));
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto item1Updated = item1;
        item1Updated.setName("updated name");
        item1Updated.setDescription("updated description");

        when(itemService.updateItem(1L, item1, 1L)).thenReturn(item1Updated);

        MvcResult result = checkItemProp(mockMvc.perform(patchJson("/items/1", item1)
                .header(USER_ID_HEADER, 1)));

        ItemDto resultItem = new ObjectMapper().readValue(result.getResponse().getContentAsString(), ItemDto.class);

        assertThat(resultItem.getName(), is("updated name"));
        assertThat(resultItem.getDescription(), is("updated description"));
    }

    @Test
    public void getItemsListTest() throws Exception {
        when(itemService.getItemsList(1L, 0, 10)).thenReturn(List.of(item1));

        checkItemListProp(mockMvc.perform(get("/items").header(USER_ID_HEADER, 1L)));
    }

    @Test
    public void searchItemsTest() throws Exception {
        when(itemService.searchItem("item1", 0, 10)).thenReturn(List.of(item1));

        checkItemListProp(mockMvc.perform(get("/items/search")
                .param("text", "item1")
                .header(USER_ID_HEADER, 1L)));
    }

    @Test
    public void addCommentTest() throws Exception {
        CommentDto comment = CommentDto.builder()
                .text("item1 comment")
                .build();

        when(commentService.add(1L, comment, 1L)).thenReturn(comment);

        mockMvc.perform(postJson("/items/1/comment", comment).header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").isString());
    }

    private static MvcResult checkItemProp(ResultActions request) throws Exception {
        return request.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.owner").isNotEmpty())
                .andExpect(jsonPath("$.available").isBoolean())
                .andReturn();
    }

    private static void checkItemListProp(ResultActions request) throws Exception {
        request.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].description").isString())
                .andExpect(jsonPath("$[0].owner").isNotEmpty())
                .andExpect(jsonPath("$[0].available").isBoolean());
    }
}
