package com.practice.shareitzeinolla.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.practice.shareitzeinolla.item.dto.ItemCreateDto;
import com.practice.shareitzeinolla.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    Long userId;

    @BeforeEach
    void createUser() throws Exception {
        User user = new User("RControllerTest", "rcontroller@test.com");
        String jsonUser = objectMapper.writeValueAsString(user);
        ResultActions postUser = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));
        String userIdJson = postUser.andReturn().getResponse().getContentAsString();
        userId = ((Integer) JsonPath.read(userIdJson, "$.id")).longValue();
    }

    @Test
    @SneakyThrows
    void requestFindById_shouldReturn_whenRequestExists() {
        // POST request
        Request request = new Request("RControllerTestFindById1");
        String jsonRequest = objectMapper.writeValueAsString(request);
        ResultActions postRequest = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header(USER_HEADER, userId));
        String requestIdJson = postRequest.andReturn().getResponse().getContentAsString();
        Long idRequest = ((Integer) JsonPath.read(requestIdJson, "$.id")).longValue();

        // GET request
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/requests/" + idRequest));

        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("RControllerTestFindById1"));
    }

    @PersistenceContext
    private EntityManager entityManager;
    @Test
    @SneakyThrows
    void requestFindById_shouldFind_whenItemsExist() {
        // POST
        Request request = new Request("RControllerTestFindById2");
        String jsonRequest = objectMapper.writeValueAsString(request);
        ResultActions postRequest = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header(USER_HEADER, userId));
        String requestIdJson = postRequest.andReturn().getResponse().getContentAsString();
        Long idRequest = ((Integer) JsonPath.read(requestIdJson, "$.id")).longValue();

        ItemCreateDto item = new ItemCreateDto(
                "RControllerTestFindById2", "RControllerTestFindById2", true, idRequest);
        String jsonItem = objectMapper.writeValueAsString(item);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem)
                .header(USER_HEADER, userId));

        entityManager.flush(); // Принудительное сохранение
        entityManager.clear(); // Очистка кэша сессии

        // GET request
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/requests/" + idRequest));

        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("RControllerTestFindById2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name").value("RControllerTestFindById2"));
    }

    @Test
    @SneakyThrows
    void requestCreate_shouldCreate_whenRequestCorrect() {
        Request request = new Request("RControllerTestCreate1");
        String jsonRequest = objectMapper.writeValueAsString(request);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header(USER_HEADER, userId));

        perform.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("RControllerTestCreate1"));
    }

    @Test
    @SneakyThrows
    void requestFindAll_shouldReturn_whenRequestExists() {
        // POST requests
        List<Request> requests = List.of(
                new Request("RControllerTestFindAll1"),
                new Request("RControllerTestFindAll2"),
                new Request("RControllerTestFindAll3")
        );
        for (Request request : requests) {
            String jsonRequest = objectMapper.writeValueAsString(request);
            mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
                    .header(USER_HEADER, userId));
        }

        // GET requests
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                .header(USER_HEADER, userId));

        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(requests.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.description == 'RControllerTestFindAll1')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.description == 'RControllerTestFindAll2')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.description == 'RControllerTestFindAll3')]").exists());
    }

    @Test
    @SneakyThrows
    void requestFindAllOtherUsers_shouldReturn_whenRequestsExists() {
        // POST other user
        User user = new User("RControllerTestFindAllOtherUsers", "rcontroller@findallotherusers.com");
        String jsonUser = objectMapper.writeValueAsString(user);
        ResultActions postUser = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));
        String userIdJson = postUser.andReturn().getResponse().getContentAsString();
        Long otherUserId = ((Integer) JsonPath.read(userIdJson, "$.id")).longValue();

        // POST requests
        List<Request> requests = List.of(
                new Request("RControllerTestFindAll1"),
                new Request("RControllerTestFindAll2"),
                new Request("RControllerTestFindAll3")
        );
        for (int i = 0; i < requests.size() - 1; i++) {
            String jsonRequest = objectMapper.writeValueAsString(requests.get(i));
            mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
                    .header(USER_HEADER, userId));
        }
        String jsonRequest = objectMapper.writeValueAsString(requests.get(requests.size() - 1));
        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header(USER_HEADER, otherUserId));

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                .header(USER_HEADER, otherUserId));

        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(requests.size() - 1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.description == 'RControllerTestFindAll1')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.description == 'RControllerTestFindAll2')]").exists());
    }
}
