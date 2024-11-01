/*
package com.practice.shareitzeinolla.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private int counter = 1;

    @Override
    public Item create(Item item) {
        item.setId(counter++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item updatedItem, int itemId) {
        items.put(itemId, updatedItem);
        return updatedItem;
    }

    @Override
    public Optional<Item> findById(int itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    @Override
    public List<Item> findAll(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public void deleteById(int itemId) {
        items.remove(itemId);
    }
}
*/
