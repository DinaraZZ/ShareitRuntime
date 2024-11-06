package com.practice.shareitzeinolla.user;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
//@AutoConfigureTestDatabase
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserJpaRepository userRepository;


    @Test
    @SneakyThrows
    void userFindById_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setName("ControllerTest");
        user.setEmail("controller@test.com");

        userRepository.save(user);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/users/1"));

        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo("ControllerTest")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Intel Core I9 9900"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.equalTo("controller@test.com")));
    }
}
