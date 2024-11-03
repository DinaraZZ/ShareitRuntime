package com.practice.shareitzeinolla.request.dto;

import com.practice.shareitzeinolla.item.dto.ItemForRequestDto;
import com.practice.shareitzeinolla.user.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestResponseDto {
    private Long id;
    private User user;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
