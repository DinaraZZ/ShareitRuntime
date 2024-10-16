package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.UserExistsException;
import com.practice.shareitzeinolla.user.dto.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User create(User user) {
        checkEmail(user);

        return userRepository.create(user);
    }

    public User update(User user, int userId) {
//        checkEmail(user);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        userMapper.merge(existingUser, user);
        return userRepository.update(existingUser, userId);
    }

    public User findById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(int userId) {
        userRepository.deleteById(userId);
    }

    private void checkEmail(User user) {
        List<String> usersEmails = findAll().stream()
                .map(User::getEmail)
                .toList();
        if (usersEmails.contains(user.getEmail())) {
            throw new UserExistsException("Пользователь с данной почтой уже существует");
        }
    }
}
