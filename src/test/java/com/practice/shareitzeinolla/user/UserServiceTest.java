package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.user.dto.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.Mockito;

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
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        User user = new User();
        user.setName("ServiceTest");
        user.setEmail("service@test.com");

        Long notExistingId = 100L;

        Mockito.when(userRepository.findById(notExistingId))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.update(user, notExistingId)
        );

        Assertions.assertEquals("Пользователь не найден.", exception.getMessage());
    }
}
