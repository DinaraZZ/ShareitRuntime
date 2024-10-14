package com.practice.shareitzeinolla.user.dto;

import com.practice.shareitzeinolla.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User fromUserCreate(UserCreateDto userCreateDto) {
        User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setName(userCreateDto.getName());
        return user;
    }

    public UserResponseDto toResponse(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getName());
        return userResponseDto;
    }
}
