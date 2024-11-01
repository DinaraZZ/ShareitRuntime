package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.user.dto.UserCreateDto;
import com.practice.shareitzeinolla.user.dto.UserMapper;
import com.practice.shareitzeinolla.user.dto.UserResponseDto;
import com.practice.shareitzeinolla.user.dto.UserUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    // patch - может поступить только одно имя, либо имейл - частичное обновление
//    private final UserService userService;
    private final UserJpaService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@Valid @RequestBody UserCreateDto user) {
        log.debug("Получен запрос POST /users: {}", user);

        return userMapper.toResponse(
                userService.create(userMapper.fromUserCreate(user)));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto update(@Valid @RequestBody UserUpdateDto user,
                                  @PathVariable Long id) {
        log.debug("Получен запрос PATCH /users/{}: {}", id, user);

        return userMapper.toResponse(
                userService.update(userMapper.fromUserUpdate(user), id));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto findById(@PathVariable Long id) {
        log.debug("Получен запрос GET /users/{}", id);

        return userMapper.toResponse(
                userService.findById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserResponseDto> findAll() {
        log.debug("Получен запрос GET /users");

        return userService.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        log.debug("Получен запрос DELETE /users/{}", id);

        userService.deleteById(id);
    }
}
