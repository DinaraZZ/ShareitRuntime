package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.UserExistsException;
import com.practice.shareitzeinolla.user.dto.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        user.setId(userId);
        checkEmail(user);
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
        if (user.getEmail() == null) {
            return;
        }
        Optional<User> optional = findAll().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findAny();
        if (optional.isPresent()) {
            if (optional.get().getId() != user.getId()) {
                throw new UserExistsException("Пользователь с данной почтой уже существует");
            }
        }
    }
}
