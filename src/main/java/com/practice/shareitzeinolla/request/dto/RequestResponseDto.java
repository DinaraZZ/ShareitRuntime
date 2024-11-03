package com.practice.shareitzeinolla.request.dto;

import com.practice.shareitzeinolla.user.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestResponseDto {
    private Long id;
    private User user;
    private String description;
    private LocalDateTime created;
}
