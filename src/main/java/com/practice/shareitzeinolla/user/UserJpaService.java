package com.practice.shareitzeinolla.user;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.UserExistsException;
import com.practice.shareitzeinolla.user.dto.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class  UserJpaService {
    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    public User create(User user) {
        checkEmail(user);

        userRepository.save(user);
        return user;
    }

    public User update(User user, Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        user.setId(userId);
        checkEmail(user);

        userMapper.merge(existingUser, user);
        userRepository.save(existingUser);

        return existingUser;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(Long userId) {
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
            if (!Objects.equals(optional.get().getId(), user.getId())) {
                throw new UserExistsException("Пользователь с данной почтой уже существует");
            }
        }
    }
}
