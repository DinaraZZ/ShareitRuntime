package com.practice.shareitzeinolla.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.shareitzeinolla.user.dto.UserCreateDto;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void handleValidation_shouldReturnBadRequest() {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-validation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("Ошибка валидации.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Ошибка валидации.")));
    }

    @Test
    @SneakyThrows
    void handleMethodArgumentValidation_shouldReturnBadRequest() {
        UserCreateDto user = new UserCreateDto();

        mockMvc.perform(MockMvcRequestBuilders.get("/test-method-argument-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("Ошибка валидации.")));
    }

    @Test
    @SneakyThrows
    void handleNotFound_shouldReturnNotFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("Не найдено.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Не найдено.")));
    }

    @Test
    @SneakyThrows
    void handleUserExists_shouldReturnInternalServerError() {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-user-exists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("Возникло исключение.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Возникло исключение.")));
    }

    @Test
    @SneakyThrows
    void handleException_shouldReturnInternalServerError() {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("Возникло исключение.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.equalTo("Возникло исключение.")));
    }
}