package com.practice.shareitzeinolla.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateDto {
    public UserCreateDto() {
    }

    public UserCreateDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @NotNull(message = "Почта пользователя не может быть пустой")
    @Email(message = "Имя пользователя не соответствует формату")
    private String email;
}
