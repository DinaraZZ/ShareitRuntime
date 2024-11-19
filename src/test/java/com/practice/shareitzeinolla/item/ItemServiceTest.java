package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.booking.BookingJpaRepository;
import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.item.dto.CommentMapper;
import com.practice.shareitzeinolla.item.dto.ItemMapper;
import com.practice.shareitzeinolla.request.RequestJpaRepository;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    ItemJpaService itemService;

    @Mock
    ItemJpaRepository itemRepository;

    @Mock
    UserJpaRepository userRepository;

    @Mock
    BookingJpaRepository bookingRepository;

    @Mock
    RequestJpaRepository requestRepository;

    @Mock
    CommentJpaRepository commentRepository;

    @BeforeEach
    void createService() {
        itemService = new ItemJpaService(itemRepository,
                new ItemMapper(bookingRepository, requestRepository, new CommentMapper()),
                userRepository, bookingRepository, commentRepository);
    }

    @Test
    void createItem_shouldCreate_whenItemCorrect() {
        User user = new User("IServiceTestCreate1", "iservice1@create.com");
        user.setId(1L);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestCreate1", "IServiceTestCreate1", true);
        Mockito.when(itemRepository.save(item))
                .thenReturn(item);

        Item itemCreated = itemService.create(item, user.getId());
        Assertions.assertNotNull(itemCreated);
        Assertions.assertEquals(user.getId(), itemCreated.getUser().getId());
        Assertions.assertEquals("IServiceTestCreate1", itemCreated.getName());
    }

    @Test
    void createItem_shouldThrowException_whenUserNotExist() {
        String expectedMessage = "Пользователь не найден";

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Item item = new Item("IServiceTestCreate2", "IServiceTestCreate2", true);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.create(item, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateItem_shouldUpdate_whenItemExists() {
        User user = new User("IServiceTestUpdate1", "iservice1@update.com");
        user.setId(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestUpdate1", "IServiceTestUpdate1", true);
        item.setUser(user);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Item updatedItem = new Item("IServiceTestUpdate2", "IServiceTestUpdate2", true);

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(updatedItem);

        Item result = itemService.update(updatedItem, 1L, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updatedItem.getName(), result.getName());
        Assertions.assertEquals(updatedItem.getDescription(), result.getDescription());
    }

    @Test
    void updateItem_shouldNotUpdate_whenUsersAreDifferent() {
        String expectedMessage = "Данный пользователь не имеет права менять товар.";

        User user = new User("IServiceTestUpdate2", "iservice2@update.com");
        user.setId(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestUpdate3", "IServiceTestUpdate3", true);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Item updatedItem = new Item("IServiceTestUpdate4", "IServiceTestUpdate4", true);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(updatedItem, 1L, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateItem_shouldThrowException_whenItemDoesNotExist() {
        String expectedMessage = "Товар не найден.";
        Long notExistingId = 100L;
        Mockito.when(itemRepository.findById(notExistingId))
                .thenReturn(Optional.empty());

        User user = new User("IServiceTestUpdate3", "iservice3@update.com");
        user.setId(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item updatedItem = new Item("IServiceTestUpdate5", "IServiceTestUpdate5", true);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(updatedItem, notExistingId, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateItem_shouldThrowException_whenUserDoesNotExist() {
        String expectedMessage = "Пользователь не найден";
        Long notExistingId = 100L;

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Item updatedItem = new Item("IServiceTestUpdate6", "IServiceTestUpdate6", true);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(updatedItem, 1L, notExistingId)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void findById_shouldFind_whenItemExists() {
        Item item = new Item("IServiceTestFind1", "IServiceTestFind1", true);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Item result = itemService.findById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(item.getName(), result.getName());
        Assertions.assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void findById_shouldThrowException_whenItemDoesNotExist() {
        String expectedMessage = "Товар не найден.";
        Long notExistingId = 100L;
        Mockito.when(itemRepository.findById(notExistingId))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.findById(notExistingId)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}