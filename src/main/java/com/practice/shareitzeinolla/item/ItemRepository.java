package com.practice.shareitzeinolla.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item updatedItem, int itemId);

    Optional<Item> findById(int itemId);

    List<Item> findAll();

    List<Item> findAll(int userId);

    void deleteById(int itemId);
}
