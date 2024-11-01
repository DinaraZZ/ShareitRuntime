package com.practice.shareitzeinolla.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUserId(Long userId);
}
