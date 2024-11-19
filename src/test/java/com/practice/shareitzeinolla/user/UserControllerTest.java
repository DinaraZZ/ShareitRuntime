package com.practice.shareitzeinolla.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    @Transactional
    void userFindById_shouldReturnUser_whenUserExists() {
        // POST user
        User user = new User("ControllerTestFindById", "controller1@findbyid.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        ResultActions postResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        String userIdJson = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(userIdJson, "$.id");

        // GET user
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/" + id));

        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("ControllerTestFindById")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("ControllerTestFindById"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.equalTo("controller1@findbyid.com")));
    }

    @Test
    @SneakyThrows
    @Transactional
    void userCreate_shouldCreateUser_whenUserCorrect() {
        User user = new User("ControllerTestCreate", "controller1@create.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        perform.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @SneakyThrows
    @Transactional
    void userFindAll_shouldFindAll_whenUsersExist() {
        // POST users
        List<User> users = List.of(
                new User("ControllerTestFindAll1", "controller1@findall.com"),
                new User("ControllerTestFindAll2", "controller2@findall.com"),
                new User("ControllerTestFindAll3", "controller3@findall.com"),
                new User("ControllerTestFindAll4", "controller4@findall.com")
        );

        ObjectMapper objectMapper = new ObjectMapper();
        for (User user : users) {
            String json = objectMapper.writeValueAsString(user);
            mockMvc.perform(MockMvcRequestBuilders.post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
        }

        // GET users
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users"));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(users.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.equalTo("ControllerTestFindAll1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].name", Matchers.equalTo("ControllerTestFindAll4")));
    }

    @Test
    @SneakyThrows
    @Transactional
    void userDeleteById_shouldDeleteUser_whenUserExists() {
        // POST user
        User user = new User("ControllerTestDeleteById", "controller1@deletebyid.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        ResultActions postResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        String userIdJson = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(userIdJson, "$.id");

        // DELETE user
        ResultActions performDelete = mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + id));
        performDelete.andExpect(MockMvcResultMatchers.status().isOk());

        // GET user
        ResultActions performGet = mockMvc.perform(MockMvcRequestBuilders.get("/users/" + id));
        performGet.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @SneakyThrows
    @Transactional
    void userUpdate_shouldUpdateUser_whenUserExists() {
        // POST user
        User user = new User("ControllerTestUpdate", "controller1@update.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        ResultActions postResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        String userIdJson = postResult.andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(userIdJson, "$.id");

        // PATCH user
        User updatedUser = new User();
        updatedUser.setEmail("controller2@update.com");
        String patchJson = objectMapper.writeValueAsString(updatedUser);

        ResultActions patchResult = mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchJson));
        patchResult.andExpect(MockMvcResultMatchers.status().isOk());

        // GET user
        ResultActions getResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/" + id));
        getResult.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("ControllerTestUpdate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.equalTo("controller2@update.com")));
    }
}
