package com.practice.shareitzeinolla.user.dto;

import com.practice.shareitzeinolla.exception.ValidationException;
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

    public User fromUserUpdate(UserUpdateDto userUpdateDto) {
        User user = new User();
        user.setName(userUpdateDto.getName());
        user.setEmail(userUpdateDto.getEmail());
        return user;
    }

    public UserResponseDto toResponse(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getName());
        return userResponseDto;
    }

    public void merge(User existingUser, User updatedUser) {
        if (updatedUser.getEmail() == null && updatedUser.getName() == null) {
            throw new ValidationException("Оба поля (имя, почта) не могут быть пустыми.");
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
    }
}
