package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.item.dto.ItemMapper;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    // todo
    private final UserService userService;

    public Item create(Item item, int userId) {
        User user = userService.findById(userId);
        item.setOwner(user);

        return itemRepository.create(item);
    }

    public Item update(Item updatedItem, int itemId, int userId) {
        User user = userService.findById(userId);
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

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public void deleteById(int itemId) {
        itemRepository.deleteById(itemId);
    }
}
