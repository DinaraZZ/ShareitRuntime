package com.practice.shareitzeinolla.item.dto;

import com.practice.shareitzeinolla.exception.ValidationException;
import com.practice.shareitzeinolla.item.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public Item fromItemCreate(ItemCreateDto itemCreateDto) {
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setOwner(itemCreateDto.getOwner());
        return item;
    }

    public Item fromItemUpdate(ItemUpdateDto itemUpdateDto) {
        Item item = new Item();
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.getAvailable());
        item.setOwner(itemUpdateDto.getOwner());
        return item;
    }

    public ItemResponseDto toResponse(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setOwner(item.getOwner());
        return itemResponseDto;
    }

    public void merge(Item existingItem, Item updatedItem) {
        if (updatedItem.getName() == null && updatedItem.getDescription() == null &&
        updatedItem.getAvailable() == null && updatedItem.getOwner() == null) {
            throw new ValidationException("Все поля не могут быть пустыми.");
        }
        if (updatedItem.getName() != null) {
            existingItem.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            existingItem.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            existingItem.setAvailable(updatedItem.getAvailable());
        }
        if (updatedItem.getOwner() != null) {
            existingItem.setOwner(updatedItem.getOwner());
        }
    }
}
