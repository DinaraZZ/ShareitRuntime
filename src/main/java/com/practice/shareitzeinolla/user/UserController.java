package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.user.dto.UserCreateDto;
import com.practice.shareitzeinolla.user.dto.UserMapper;
import com.practice.shareitzeinolla.user.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    // patch - может поступить только одно имя, либо имейл - частичное обновление
    private final UserRepository userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@Valid @RequestBody UserCreateDto user) {
        return userMapper.toResponse(userMapper.fromUserCreate(user));
    }

//    @PatchMapping
}
