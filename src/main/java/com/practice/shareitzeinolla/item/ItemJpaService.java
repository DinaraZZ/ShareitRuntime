package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.booking.Booking;
import com.practice.shareitzeinolla.booking.BookingJpaRepository;
import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.ValidationException;
import com.practice.shareitzeinolla.item.dto.ItemMapper;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemJpaService {
    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final CommentJpaRepository commentJpaRepository;

    public Item create(Item item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        item.setUser(user);

        itemRepository.save(item);
        return item;
    }

    public Item update(Item updatedItem, Long itemId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedItem.setUser(user);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        if (user != existingItem.getUser()) {
            throw new NotFoundException("Данный пользователь не имеет права менять товар.");
        }
        itemMapper.merge(existingItem, updatedItem);

        itemRepository.save(existingItem);

        return existingItem;
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));
    }

    public List<Item> findAll(Long userId, Integer fromIndex, Integer size) {
        Pageable pageable = PageRequest.of(fromIndex / size, size);

        return itemRepository.findAllByUserId(userId, pageable);
    }

    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    public List<Item> search(String text, Integer fromIndex, Integer size) {
//        Pageable pageable = PageRequest.of(fromIndex / size, size);

        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findAll().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(text.toLowerCase()) |
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                                && item.getAvailable() == true)
                .toList();
    }

    public Comment addCommentary(Long userId, Long itemId, Comment comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        comment.setUser(user);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));
        comment.setItem(existingItem);

        Booking booking = bookingRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new ValidationException("Данный пользователь не может оставить отзыв к этому товару."));

        if (booking.getToDate().isBefore(LocalDateTime.now())) {
            commentJpaRepository.save(comment);
        } else {
            throw new ValidationException(
                    "Пользователь может оставлять комментарии только после окончания бронирования.");
        }

        return comment;
    }
}
