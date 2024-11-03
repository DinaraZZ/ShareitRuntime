package com.practice.shareitzeinolla.request;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestJpaService {
    private final RequestJpaRepository requestRepository;
    private final UserJpaRepository userRepository;

    public Request create(Request request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        request.setUser(user);

        requestRepository.save(request);
        return request;
    }
}
