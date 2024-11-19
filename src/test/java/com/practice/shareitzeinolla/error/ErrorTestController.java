package com.practice.shareitzeinolla.error;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.UserExistsException;
import com.practice.shareitzeinolla.exception.ValidationException;
import com.practice.shareitzeinolla.user.dto.UserCreateDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorTestController {
    @GetMapping("/test-validation")
    public String testValidation() {
        throw new ValidationException("Ошибка валидации.");
    }

    @GetMapping("/test-method-argument-validation")
    public String testMethodArgumentValidation(@Valid @RequestBody UserCreateDto userCreateDto) {
        return "Пользователь создан";
    }

    @GetMapping("/test-not-found")
    public String testNotFound() {
        throw new NotFoundException("Не найдено.");
    }

    @GetMapping("/test-user-exists")
    public String testUserExists() {
        throw new UserExistsException("Возникло исключение.");
    }

    @GetMapping("/test-exception")
    public String testException() throws Exception {
        throw new Exception("Возникло исключение.");
    }
}
