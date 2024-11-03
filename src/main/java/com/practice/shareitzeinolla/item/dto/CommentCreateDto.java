package com.practice.shareitzeinolla.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateDto {
    @NotNull(message = "Отзыв не может быть пустым.")
    private String text;
}
