package com.practice.shareitzeinolla.item.dto;

import com.practice.shareitzeinolla.item.Item;
import lombok.Data;

@Data
public class ItemForRequestDto {
    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;

    public static ItemForRequestDto of(Item item) {
        ItemForRequestDto dto = new ItemForRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setRequestId(item.getRequest().getId());
        dto.setAvailable(item.getAvailable());
        return dto;
    }
}

