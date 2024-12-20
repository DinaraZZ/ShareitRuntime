package com.practice.shareitzeinolla.item.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentResponseDto {
    private Long id;
    private String authorName;
    private Long itemId;
    private String text;
    private LocalDate created;
}
