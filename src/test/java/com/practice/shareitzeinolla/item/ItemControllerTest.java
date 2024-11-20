package com.practice.shareitzeinolla.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.practice.shareitzeinolla.booking.dto.BookingCreateDto;
import com.practice.shareitzeinolla.item.dto.ItemCreateDto;
import com.practice.shareitzeinolla.request.Request;
import com.practice.shareitzeinolla.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    Long userId;

    @BeforeEach
    void createUser() throws Exception {
        User user = new User("IControllerTest", "icontroller@test.com");
        String jsonUser = objectMapper.writeValueAsString(user);
        ResultActions postUser = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));
        String userIdJson = postUser.andReturn().getResponse().getContentAsString();
        userId = ((Integer) JsonPath.read(userIdJson, "$.id")).longValue();
        postUser.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @SneakyThrows
    void itemCreate_shouldCreate_whenItemCorrect() {
        Item item = new Item("IControllerTestCreate1", "IControllerTestCreate1", true);
        String jsonItem = objectMapper.writeValueAsString(item);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem)
                .header(USER_HEADER, userId));

        perform.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("IControllerTestCreate1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("IControllerTestCreate1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    @SneakyThrows
    void itemCreate_shouldThrow_whenRequestDoesNotExist() {
        long notExistingId = 100L;
        String expectedMessage = "Запрос не найден.";

        ItemCreateDto item = new ItemCreateDto("IControllerTestCreate2", "IControllerTestCreate2", true, notExistingId);
        String jsonItem = objectMapper.writeValueAsString(item);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem)
                .header(USER_HEADER, userId));

        perform.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedMessage));
    }

    @Test
    @SneakyThrows
    void itemUpdate_shouldUpdate_whenItemExists() {
        // POST item
        Item item = new Item("IControllerTestUpdate1", "IControllerTestUpdate1", true);
        String postJson = objectMapper.writeValueAsString(item);
        ResultActions postResult = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson)
                .header(USER_HEADER, userId));
        String itemIdJson = postResult.andReturn().getResponse().getContentAsString();
        Long itemId = ((Integer) JsonPath.read(itemIdJson, "$.id")).longValue();

        // PATCH item
        Item updatedItem = new Item();
        updatedItem.setName("IControllerTestUpdate2");
        String patchJson = objectMapper.writeValueAsString(updatedItem);
        ResultActions patchResult = mockMvc.perform(MockMvcRequestBuilders.patch("/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchJson)
                .header(USER_HEADER, userId));
        patchResult.andExpect(MockMvcResultMatchers.status().isOk());

        // GET item
        ResultActions getResult = mockMvc.perform(MockMvcRequestBuilders.get("/items/" + itemId));
        getResult.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("IControllerTestUpdate2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("IControllerTestUpdate1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.equalTo(true)));
    }

    @Test
    @SneakyThrows
    void itemFindById_shouldFind_whenItemExists() {
        // POST item
        Item item = new Item("IControllerTestFindById", "IControllerTestFindById", true);
        String postJson = objectMapper.writeValueAsString(item);
        ResultActions postResult = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson)
                .header(USER_HEADER, userId)
                .header(USER_HEADER, userId));
        String itemIdJson = postResult.andReturn().getResponse().getContentAsString();
        Long itemId = ((Integer) JsonPath.read(itemIdJson, "$.id")).longValue();

        // GET item
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/items/" + itemId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("IControllerTestFindById")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("IControllerTestFindById")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.equalTo(true)));
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @SneakyThrows
    void itemFindById_shouldFind_whenCommentsExist() {
        // POST item
        Item item = new Item("IControllerTestFindById2", "IControllerTestFindById2", true);
        String postJson = objectMapper.writeValueAsString(item);
        ResultActions postResult = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson)
                .header(USER_HEADER, userId)
                .header(USER_HEADER, userId));
        String itemIdJson = postResult.andReturn().getResponse().getContentAsString();
        Long itemId = ((Integer) JsonPath.read(itemIdJson, "$.id")).longValue();

        // POST booking
        BookingCreateDto booking = new BookingCreateDto(itemId,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().plusSeconds(1));
        String postBookingJson = objectMapper.writeValueAsString(booking);
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBookingJson)
                .header(USER_HEADER, userId));

        // comment
        Comment comment = new Comment("IControllerTestFindByIdText");
        String commentJson = objectMapper.writeValueAsString(comment);

        // Ожидание окончания бронирования
        Thread.sleep(1100);

        // POST comment
        mockMvc.perform(MockMvcRequestBuilders.post("/items/" + itemId + "/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentJson)
                .header(USER_HEADER, userId));

        entityManager.flush(); // Принудительное сохранение
        entityManager.clear(); // Очистка кэша сессии

        // GET item
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/items/" + itemId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("IControllerTestFindById2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("IControllerTestFindById2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.equalTo(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comments[0].text", Matchers.equalTo("IControllerTestFindByIdText")));
    }

    @Test
    @SneakyThrows
    void itemFindAll_shouldReturn_whenItemsExist() {
        // POST items
        List<Item> items = List.of(
                new Item("IControllerTestFindAll1", "IControllerTestFindAll1", true),
                new Item("IControllerTestFindAll2", "IControllerTestFindAll2", true),
                new Item("IControllerTestFindAll3", "IControllerTestFindAll3", true)
        );
        for (Item item : items) {
            String postJson = objectMapper.writeValueAsString(item);
            mockMvc.perform(MockMvcRequestBuilders.post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(postJson)
                    .header(USER_HEADER, userId));
        }

        // GET items
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/items")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(items.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.name == 'IControllerTestFindAll1')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.name == 'IControllerTestFindAll2')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.name == 'IControllerTestFindAll3')]").exists());
    }

    @Test
    @SneakyThrows
    void itemDeleteById_shouldDelete_ifItemExists() {
        // POST item
        Item item = new Item("IControllerTestDeleteById", "IControllerTestDeleteById", true);
        String postJson = objectMapper.writeValueAsString(item);
        ResultActions postResult = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson)
                .header(USER_HEADER, userId));
        String itemIdJson = postResult.andReturn().getResponse().getContentAsString();
        long itemId = ((Integer) JsonPath.read(itemIdJson, "$.id")).longValue();

        // DELETE item
        ResultActions performDelete = mockMvc.perform(MockMvcRequestBuilders.delete("/items/" + itemId));
        performDelete.andExpect(MockMvcResultMatchers.status().isOk());

        // GET item
        ResultActions performGet = mockMvc.perform(MockMvcRequestBuilders.get("/items/" + itemId)
                .header(USER_HEADER, userId));
        performGet.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @SneakyThrows
    void itemSearch_shouldReturn_whenItemsExist() {
        // POST items
        List<Item> items = List.of(
                new Item("IControllerTestSearch1", "IControllerTestSearch1", true),
                new Item("IControllerTestSearch2", "IControllerTestSearch2", true),
                new Item("IControllerTestSearch3", "IControllerTestSearch3", true)
        );
        for (Item item : items) {
            String postJson = objectMapper.writeValueAsString(item);
            mockMvc.perform(MockMvcRequestBuilders.post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(postJson)
                    .header(USER_HEADER, userId));
        }

        // GET items
        String text = "testsearch";
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=" + text));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(items.size())));
    }

    @Test
    @SneakyThrows
    void itemAddCommentary_shouldReturn_whenItemExist() {
        // POST item
        Item item = new Item("IControllerTestAddCommentary", "IControllerTestAddCommentary", true);
        String postItemJson = objectMapper.writeValueAsString(item);
        ResultActions postItemResult = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postItemJson)
                .header(USER_HEADER, userId));
        String itemIdJson = postItemResult.andReturn().getResponse().getContentAsString();
        long itemId = ((Integer) JsonPath.read(itemIdJson, "$.id")).longValue();

        // POST booking
        BookingCreateDto booking = new BookingCreateDto(itemId, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusSeconds(1));
        String postBookingJson = objectMapper.writeValueAsString(booking);
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBookingJson)
                .header(USER_HEADER, userId));

        // comment
        Comment comment = new Comment("IControllerTestAddCommentaryText");
        String commentJson = objectMapper.writeValueAsString(comment);

        // Ожидание окончания бронирования
        Thread.sleep(1100);

        // POST comment
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/items/" + itemId + "/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentJson)
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.equalTo("IControllerTestAddCommentaryText")));
    }
}