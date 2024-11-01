package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.item.dto.ItemMapper;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemJpaService {
    private final ItemJpaRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserJpaRepository userRepository;

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
            throw new NotFoundException("");
        }
        itemMapper.merge(existingItem, updatedItem);

        itemRepository.save(existingItem);

        return existingItem;
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));
    }

    public List<Item> findAll(Long userId) {
        return itemRepository.findAllByUserId(userId);
    }

    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    public List<Item> search(String text) {
        if (text == null || text.isEmpty()) return Collections.emptyList();
        return itemRepository.findAll().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(text.toLowerCase()) |
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                                && item.getAvailable() == true)
                .toList();
    }
}
