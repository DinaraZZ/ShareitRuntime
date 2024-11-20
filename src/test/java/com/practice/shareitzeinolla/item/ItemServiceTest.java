package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.booking.Booking;
import com.practice.shareitzeinolla.booking.BookingJpaRepository;
import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.ValidationException;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
    void updateItem_shouldThrowException_whenFieldsAreNull() {
        String expectedMessage = "Все поля не могут быть пустыми.";

        User user = new User("IServiceTestUpdate1", "iservice1@update.com");
        user.setId(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestUpdate1", "IServiceTestUpdate1", true);
        item.setUser(user);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Item updatedItem = new Item(null, null, null);
        updatedItem.setUser(null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.update(updatedItem, item.getId(), user.getId()));

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateItem_shouldNotThrowException_whenNameIsNull() {
        User user = new User("IServiceTestUpdate5", "iservice5@update.com");
        user.setId(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestUpdate5", "IServiceTestUpdate5", true);
        item.setUser(user);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Item updatedItem = new Item(null, "IServiceTestUpdate6", false);

        Assertions.assertDoesNotThrow(
                () -> itemService.update(updatedItem, item.getId(), user.getId()));
    }

    @Test
    void updateItem_shouldNotThrowException_whenDescriptionIsNull() {
        User user = new User("IServiceTestUpdate6", "iservice6@update.com");
        user.setId(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestUpdate6", "IServiceTestUpdate6", true);
        item.setUser(user);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Item updatedItem = new Item("IServiceTestUpdate7", null, false);

        Assertions.assertDoesNotThrow(
                () -> itemService.update(updatedItem, item.getId(), user.getId()));
    }

    @Test
    void updateItem_shouldNotThrowException_whenAvailableIsNull() {
        User user = new User("IServiceTestUpdate7", "iservice7@update.com");
        user.setId(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestUpdate7", "IServiceTestUpdate7", true);
        item.setUser(user);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Item updatedItem = new Item("IServiceTestUpdate8", "IServiceTestUpdate8", null);

        Assertions.assertDoesNotThrow(
                () -> itemService.update(updatedItem, item.getId(), user.getId()));
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

    @Test
    void findAll_shouldReturn_whenItemsExist() {
        List<Item> items = List.of(
                new Item("IServiceTestFindAll1", "IServiceTestFindAll1", true),
                new Item("IServiceTestFindAll2", "IServiceTestFindAll2", true),
                new Item("IServiceTestFindAll3", "IServiceTestFindAll3", true)
        );
        Mockito.when(itemRepository.findAllByUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(items);

        List<Item> foundItems = itemRepository.findAllByUserId(1L, Pageable.unpaged());

        Assertions.assertNotNull(foundItems);
        Assertions.assertEquals(items.size(), foundItems.size());
    }

    @Test
    void findAll_shouldNotReturn_whenItemsDoNotExist() {
        int expectedSize = 0;
        Mockito.when(itemRepository.findAllByUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<Item> foundItems = itemRepository.findAllByUserId(1L, Pageable.unpaged());

        Assertions.assertNotNull(foundItems);
        Assertions.assertEquals(expectedSize, foundItems.size());
    }

    @Test
    void deleteById_shouldDelete_whenItemExists() {
        Long existingId = 1L;

        Mockito.doNothing().when(itemRepository).deleteById(existingId);

        itemService.deleteById(existingId);

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(existingId);
    }

    @Test
    void search_shouldReturn_whenItemsExist() {
        List<Item> items = List.of(
                new Item("IServiceTestSearch1", "IServiceTestSearch1", true),
                new Item("IServiceTestSearch2", "IServiceTestSearch2", true),
                new Item("IServiceTestSearch3", "IServiceTestSearch3", true)
        );

        Mockito.when(itemRepository.findAll())
                .thenReturn(items);

        List<Item> result = itemService.search("TestSearch", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(items.size(), result.size());
        Assertions.assertTrue(result.stream().allMatch(item ->
                item.getName().toLowerCase().contains("testsearch") ||
                        item.getDescription().toLowerCase().contains("testsearch")));
        Assertions.assertTrue(result.stream().allMatch(item -> item.getAvailable() == true));
    }

    @Test
    void search_shouldNotReturnItems_whenAvailableIsFalse() {
        Item availableItem = new Item(
                "IServiceTestSearch4", "IServiceTestSearch4", true);
        Item unavailableItem = new Item(
                "IServiceTestSearch5", "IServiceTestSearch5", false);

        List<Item> items = List.of(availableItem, unavailableItem);
        Mockito.when(itemRepository.findAll())
                .thenReturn(items);

        List<Item> result = itemService.search("TestSearch", 0, 10);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(availableItem));
        Assertions.assertFalse(result.contains(unavailableItem));
    }


    @Test
    void search_shouldNotReturn_whenTextIsEmpty() {
        List<Item> result = itemService.search("", 0, 10);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void search_shouldNotReturn_whenTextIsNull() {
        List<Item> result = itemService.search(null, 0, 10);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void search_shouldNotReturn_whenNotMatching() {
        List<Item> items = List.of(
                new Item("IServiceTestSearch4", "IServiceTestSearch4", true),
                new Item("IServiceTestSearch5", "IServiceTestSearch5", true),
                new Item("IServiceTestSearch6", "IServiceTestSearch6", true)
        );
        Mockito.when(itemRepository.findAll())
                .thenReturn(items);

        List<Item> result = itemService.search("noMatch", 0, 10);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void addCommentary_shouldAdd_whenCorrect() throws InterruptedException {
        User user = new User("IServiceTestAddCommentary", "iservice1@add.com");
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestAddCommentary", "IServiceTestAddCommentary", true);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Booking booking = new Booking(item,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().plusSeconds(1));
        Mockito.when(bookingRepository.findByUserIdAndItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Thread.sleep(1100);

        Comment comment = new Comment("IServiceTestAddCommentaryText");
        Mockito.when(commentRepository.save(comment))
                .thenReturn(comment);

        Comment result = itemService.addCommentary(1L, 1L, comment);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(comment.getText(), result.getText());
    }

    @Test
    void addCommentary_shouldThrowException_whenUserDoesNotExist() {
        String expectedMessage = "Пользователь не найден";
        Long notExistingId = 100L;

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addCommentary(notExistingId, notExistingId, new Comment())
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void addCommentary_shouldThrowException_whenItemDoesNotExist() {
        String expectedMessage = "Товар не найден.";
        Long notExistingId = 100L;

        User user = new User("IServiceTestAddCommentary2", "iservice2@add.com");
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.addCommentary(1L, notExistingId, new Comment())
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void addCommentary_shouldThrowException_whenUserDoesNotMatch() {
        String expectedMessage = "Данный пользователь не может оставить отзыв к этому товару.";

        User user = new User("IServiceTestAddCommentary3", "iservice3@add.com");
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestAddCommentary2", "IServiceTestAddCommentary2", true);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findByUserIdAndItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addCommentary(1L, 1L, new Comment())
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void addCommentary_shouldThrowException_whenBookingHasNotEnded() {
        String expectedMessage = "Пользователь может оставлять комментарии только после окончания бронирования.";

        User user = new User("IServiceTestAddCommentary4", "iservice4@add.com");
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Item item = new Item("IServiceTestAddCommentary3", "IServiceTestAddCommentary3", true);
        item.setId(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Booking booking = new Booking(item,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().plusDays(1));
        Mockito.when(bookingRepository.findByUserIdAndItemId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addCommentary(1L, 1L, new Comment())
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}