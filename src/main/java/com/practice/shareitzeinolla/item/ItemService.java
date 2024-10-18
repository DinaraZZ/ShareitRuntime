package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.item.dto.ItemMapper;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    // todo
    private final UserRepository userRepository;

    public Item create(Item item, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        item.setOwner(user);

        return itemRepository.create(item);
    }

    public Item update(Item updatedItem, int itemId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedItem.setOwner(user);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));

        if (user != existingItem.getOwner()) {
            throw new NotFoundException("");
        }
        itemMapper.merge(existingItem, updatedItem);

        return itemRepository.update(existingItem, itemId);
    }

    public Item findById(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товар не найден."));
    }

    public List<Item> findAll(int userId) {
        return itemRepository.findAll(userId);
    }

    public void deleteById(int itemId) {
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
