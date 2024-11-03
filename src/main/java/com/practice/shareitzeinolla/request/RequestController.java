package com.practice.shareitzeinolla.request;

import com.practice.shareitzeinolla.request.dto.RequestCreateDto;
import com.practice.shareitzeinolla.request.dto.RequestMapper;
import com.practice.shareitzeinolla.request.dto.RequestResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private static final Logger log = LoggerFactory.getLogger(RestController.class);
    private final RequestJpaService requestService;
    private final RequestMapper requestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponseDto create(@RequestHeader(name = USER_HEADER) Long userId,
                                     @Valid @RequestBody RequestCreateDto request) {
        log.debug("Получен запрос POST userId: {}, /requests: {}", userId, request);

        return requestMapper.toResponse(
                requestService.create(requestMapper.fromRequestCreate(request), userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestResponseDto> findAll(@RequestHeader(name = USER_HEADER) Long userId) {
        log.debug("Получен запрос GET userId: {}, /requests: {}", userId);

        return requestService.findAll(userId).stream()
                .map(requestMapper::toResponse)
                .toList();
    }
}
