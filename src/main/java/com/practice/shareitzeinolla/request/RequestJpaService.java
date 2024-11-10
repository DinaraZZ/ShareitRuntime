package com.practice.shareitzeinolla.request;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    public List<Request> findAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Request> requests = requestRepository.findAllByUserIdOrderByCreatedDesc(userId);
        return requests;
    }

    public Request findById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
    }

    public List<Request> findAllOtherUsers(Long userId, Integer fromIndex, Integer size) {
        Pageable pageable = PageRequest.of(fromIndex / size, size);

        return requestRepository.findAllExceptUserId(userId, pageable);
    }
}
