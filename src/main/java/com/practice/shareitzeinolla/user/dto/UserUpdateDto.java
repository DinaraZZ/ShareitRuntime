package com.practice.shareitzeinolla.user.dto;

import jakarta.validation.constraints.Email;

public class UserUpdateDto {
    private String name;

    @Email
    private String email;
}
