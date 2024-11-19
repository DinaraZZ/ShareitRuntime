package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@AutoConfigureTestDatabase
@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemJpaRepository itemRepository;

    @Autowired
    UserJpaRepository userRepository;

    @Test
    void saveItem_shouldSave_whenItemCorrect() {
        Item item = new Item("IRepositoryTestSave1", "IRepositoryTestSave1", true);
        itemRepository.save(item);

        Item savedItem = itemRepository.findById(item.getId()).orElseThrow();
        Assertions.assertEquals(item.getName(), savedItem.getName());
    }

    @Test
    void findById_shouldFind_whenItemExists() {
        Item item = new Item("IRepositoryTestFindById1", "IRepositoryTestFindById1", true);
        itemRepository.save(item);

        Item foundItem = itemRepository.findById(item.getId()).orElseThrow();
        Assertions.assertEquals(item.getId(), foundItem.getId());
    }

    @Test
    void findById_shouldNotFind_whenItemDoesNotExist() {
        Item item = new Item("IRepositoryTestFindById2", "IRepositoryTestFindById2", true);
        itemRepository.save(item);
        String expectedMessage = "Товар не найден";
        Long notExistingId = 100L;

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRepository.findById(notExistingId)
                        .orElseThrow(() -> new NotFoundException(expectedMessage))
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findAll_shouldFindAll_whenItemsExist() {
        List<Item> items = List.of(
                new Item("IRepositoryTestFindAll1", "IRepositoryTestFindAll1", true),
                new Item("IRepositoryTestFindAll2", "IRepositoryTestFindAll2", true)
        );
        int expectedSize = items.size();
        itemRepository.saveAll(items);

        List<Item> foundItems = itemRepository.findAll();

        Assertions.assertEquals(expectedSize, foundItems.size());
    }

    @Test
    void findAll_shouldNotFindAll_whenItemsDoNotExist() {
        int expectedSize = 0;

        List<Item> foundItems = itemRepository.findAll();

        Assertions.assertEquals(expectedSize, foundItems.size());
    }

    @Test
    void deleteById_shouldDelete_whenItemExists() {
        long id = 1;
        String expectedMessage = "Товар не найден";
        Item item = new Item("IRepositoryTestDeleteById1", "IRepositoryTestDeleteById1", true);
        item.setId(id);
        itemRepository.save(item);

        itemRepository.deleteById(item.getId());
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(expectedMessage))
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findAllByUserId_shouldFind_whenUserExistsAndItemsExist() {
        User user = new User("IRepositoryTestFindAll1", "irepository1@findall.com");
        userRepository.save(user);

        Item item1 = new Item("IRepositoryTestFindAll2", "IRepositoryTestFindAll2", true);
        item1.setUser(user);
        Item item2 = new Item("IRepositoryTestFindAll3", "IRepositoryTestFindAll3", true);
        item2.setUser(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
        int expectedSize = 2;

        Pageable pageable = PageRequest.of(0, 10);
        List<Item> foundItems = itemRepository.findAllByUserId(user.getId(), pageable);

        Assertions.assertEquals(expectedSize, foundItems.size());
    }

    @Test
    void findAll_shouldNotFind_whenUserExistsAndItemsDoNotExist() {
        User user = new User("IRepositoryTestFindAll2", "irepository2@findall.com");
        userRepository.save(user);
        int expectedSize = 0;

        Pageable pageable = PageRequest.of(0, 10);
        List<Item> foundItems = itemRepository.findAllByUserId(user.getId(), pageable);

        Assertions.assertEquals(expectedSize, foundItems.size());
    }

    @Test
    void findAll_shouldNotFind_whenUserDoesNotExist() {
        long notExistingId = 100L;
        int expectedSize = 0;

        Pageable pageable = PageRequest.of(0, 10);
        List<Item> foundItems = itemRepository.findAllByUserId(notExistingId, pageable);

        Assertions.assertEquals(expectedSize, foundItems.size());
    }
}
