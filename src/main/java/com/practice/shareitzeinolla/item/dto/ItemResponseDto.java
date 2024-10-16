package com.practice.shareitzeinolla.item.dto;

import com.practice.shareitzeinolla.user.User;
import lombok.Data;

@Data
public class ItemResponseDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
