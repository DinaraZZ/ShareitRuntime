package com.practice.shareitzeinolla.request;

import com.practice.shareitzeinolla.request.dto.RequestCreateDto;
import com.practice.shareitzeinolla.request.dto.RequestMapper;
import com.practice.shareitzeinolla.request.dto.RequestResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
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

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public RequestResponseDto findById(@PathVariable Long requestId) {
        log.debug("Получен запрос GET /requests/{}", requestId);
        return requestMapper.toResponse(
                requestService.findById(requestId));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestResponseDto> findAllOtherUsers(@RequestHeader(name = USER_HEADER) Long userId,
                                                            @RequestParam(name = "from", defaultValue = "0") Integer fromIndex,
                                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен запрос GET /requests/all?from={}&size={}", fromIndex, size);

        return requestService.findAllOtherUsers(userId, fromIndex, size).stream()
                .map(requestMapper::toResponse)
                .toList();
    }
}
