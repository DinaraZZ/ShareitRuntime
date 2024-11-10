package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.UserExistsException;
import com.practice.shareitzeinolla.user.dto.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    UserJpaService userService;

    @Mock
    UserJpaRepository userRepository;

    @Mock
    UserMapper userMapper;

    @BeforeEach
    void createService() {
        userService = new UserJpaService(userRepository, userMapper);
    }

    @Test
    void createUser_shouldCreate_whenUserCorrect() { // ?
        User user = new User("ServiceTestCreate", "service@test.com");

//        Mockito.when(userService.create(user)).thenReturn(user);
        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        User createdUser = userService.create(user);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals("ServiceTestCreate", createdUser.getName());
        Assertions.assertEquals("service@test.com", createdUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1))
                .save(Mockito.any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        User existingUser = new User("ServiceTestCreate", "service@test.com");
        existingUser.setId(1L);
        User newUser = new User("ServiceTestCreateNew", "service@test.com");

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(existingUser));

        final UserExistsException exception = Assertions.assertThrows(
                UserExistsException.class,
                () -> userService.create(newUser)
        );

        Assertions.assertEquals("Пользователь с данной почтой уже существует", exception.getMessage());
    }

    /*@Test
    void updateUser_shouldUpdate_whenUserExists() { // ?
        Long existingId = 1L;
        User existingUser = new User("ServiceTestUpdate1", "service1@test.com");
        existingUser.setId(existingId);

        User updatedUser = new User("ServiceTestUpdate2", "service2@test.com");

        Mockito.when(userRepository.findById(existingId))
                .thenReturn(Optional.of(existingUser));

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(updatedUser);

        User result = userService.update(updatedUser, existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ServiceTestUpdate2", result.getName());
        Assertions.assertEquals("service2@test.com", result.getEmail());
    }*/

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        User user = new User("ServiceTest", "service@test.com");

        Long notExistingId = 100L;

        Mockito.when(userRepository.findById(notExistingId))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.update(user, notExistingId)
        );

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    void findByIdUser_shouldFind_whenUserCorrect() {
        User user = new User("ServiceTestFindById", "service@test.com");
        Long existingId = 1L;

        Mockito.when(userRepository.findById(existingId))
                .thenReturn(Optional.of(user));

        User foundUser = userService.findById(existingId);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("ServiceTestFindById", foundUser.getName());
        Assertions.assertEquals("service@test.com", foundUser.getEmail());
    }

    @Test
    void findByIdUser_shouldThrowException_whenUserDoesNotExist() {
        Long notExistingId = 100L;

        Mockito.when(userRepository.findById(notExistingId))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.findById(notExistingId)
        );

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    void findAllUsers_shouldFindAllUsers_whenUsersExists() {
        List<User> users = List.of(
                new User("ServiceTestFindAll1", "service1@test.com"),
                new User("ServiceTestFindAll2", "service2@test.com"),
                new User("ServiceTestFindAll3", "service3@test.com"),
                new User("ServiceTestFindAll4", "service4@test.com")
        );

        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        List<User> foundUsers = userService.findAll();

        Assertions.assertNotNull(foundUsers);
        Assertions.assertEquals(users.size(), foundUsers.size());
    }

    @Test
    void deleteByIdUser_shouldDelete_whenUserExists() {
        User user = new User("ServiceTestDelete", "service@test.com");
        Long existingId = 1L;

        Mockito.doNothing().when(userRepository).deleteById(existingId);

        userService.deleteById(existingId);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(existingId);
    }

    @Test
    void checkEmail_shouldReturn_whenEmailIsNull() {
        User user = new User("ServiceTestCheckEmail", null);

        userService.create(user);

        Mockito.verify(userRepository, Mockito.never())
                .findAll();
    }
}
