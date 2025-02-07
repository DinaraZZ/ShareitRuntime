package com.practice.shareitzeinolla.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCreateDto {
    public RequestCreateDto() {
    }

    public RequestCreateDto(String description) {
        this.description = description;
    }

    @NotNull(message = "Описание не может быть пустым")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
