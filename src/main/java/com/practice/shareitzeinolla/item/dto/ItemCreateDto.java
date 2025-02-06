package com.practice.shareitzeinolla.item.dto;

import com.practice.shareitzeinolla.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCreateDto {
    public ItemCreateDto() {
    }

    public ItemCreateDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemCreateDto(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

    @NotBlank(message = "Название товара не может быть пустым")
    private String name;

    @NotNull(message = "Описание товара не может быть пустым")
    private String description;

    @NotNull(message = "Доступность товара не может быть пустой")
    private Boolean available;

    private User user;

    private Long requestId;
}
