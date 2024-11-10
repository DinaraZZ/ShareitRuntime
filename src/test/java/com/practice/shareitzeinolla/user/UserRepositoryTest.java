package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@AutoConfigureTestDatabase
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserJpaRepository userRepository;

    @Test
    void saveUser_shouldSave_whenCorrectUserGiven() {
        User user = new User("RepositoryTest", "repository@test.com");

        userRepository.save(user);

        User savedUser = userRepository.findById(user.getId()).orElseThrow();

        Assertions.assertEquals(user.getName(), savedUser.getName());
    }

    @Test
    void findByIdUser_shouldFind_whenUserExists() {
        User user = new User("RepositoryTestFindById", "repository@test.com");

        userRepository.save(user);

        User foundUser = userRepository.findById(user.getId()).orElseThrow();

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getName(), foundUser.getName());
    }

    @Test
    void findByIdUser_shouldThrowException_whenUserDoesNotExist() {
        User user = new User("RepositoryTestFindById", "repository@test.com");
        Long notExistingId = 100L;

        userRepository.save(user);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userRepository.findById(notExistingId)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден."))
        );

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }

    @Test
    void findAllUsers_shouldFindAllUsers_whenUserExists() {
        List<User> users = List.of(
                new User("RepositoryTestFindAll1", "repository1@test.com"),
                new User("RepositoryTestFindAll2", "repository2@test.com"),
                new User("RepositoryTestFindAll3", "repository3@test.com"),
                new User("RepositoryTestFindAll4", "repository4@test.com")
        );

        userRepository.saveAll(users);

        List<User> foundUsers = userRepository.findAll();
        Assertions.assertNotNull(foundUsers);
        Assertions.assertEquals(users.size(), foundUsers.size());
    }

    @Test
    void deleteByIdUser_shouldDelete_whenUserExists() {
        User user = new User("RepositoryTestDeleteById", "repository@test.com");

        userRepository.save(user);
        userRepository.deleteById(user.getId());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userRepository.findById(user.getId())
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден.")));

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }
}
