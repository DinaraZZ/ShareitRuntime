package com.practice.shareitzeinolla.item.dto;

import com.practice.shareitzeinolla.booking.BookingJpaRepository;
import com.practice.shareitzeinolla.booking.BookingStatus;
import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.ValidationException;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.request.Request;
import com.practice.shareitzeinolla.request.RequestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingJpaRepository bookingRepository;
    private final RequestJpaRepository requestRepository;
    private final CommentMapper commentMapper;

    public Item fromItemCreate(ItemCreateDto itemCreateDto) {
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setUser(itemCreateDto.getUser());

        if (itemCreateDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден."));
            item.setRequest(request);
            request.getItems().add(item);
        }

        return item;
    }

    public Item fromItemUpdate(ItemUpdateDto itemUpdateDto) {
        Item item = new Item();
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.getAvailable());
        item.setUser(itemUpdateDto.getUser());
        return item;
    }

    public ItemResponseDto toResponse(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setUser(item.getUser());

        if (item.getComments() != null) {
            List<CommentResponseDto> comments = item.getComments().stream()
                    .map(commentMapper::toResponse).toList();
            itemResponseDto.setComments(comments);
        }
        itemResponseDto.setLastBooking(
                bookingRepository.findLastBooking(item.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                        .orElse(null));

        if (item.getRequest() != null) {
            itemResponseDto.setRequestId(item.getRequest().getId());
        }

        return itemResponseDto;
    }

    public void merge(Item existingItem, Item updatedItem) {
        if (updatedItem.getName() == null && updatedItem.getDescription() == null &&
                updatedItem.getAvailable() == null) {
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
        existingItem.setUser(updatedItem.getUser());
    }
}
