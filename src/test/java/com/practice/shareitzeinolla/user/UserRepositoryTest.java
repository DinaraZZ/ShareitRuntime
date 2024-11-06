package com.practice.shareitzeinolla.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    UserJpaRepository userRepository;

    @Test
    void saveUser_shouldSave_whenCorrectUserGiven() {
        User user = new User();
        user.setName("RepositoryTest");
        user.setEmail("repository@test.com");

        userRepository.save(user);

        User savedUser = userRepository.findById(1L).orElseThrow();

        Assertions.assertEquals(user.getName(), savedUser.getName());
    }
}
