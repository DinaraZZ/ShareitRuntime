package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.item.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    //    private final ItemService itemService;
    private final ItemJpaService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(@RequestHeader(name = USER_HEADER) Long userId,
                                  @Valid @RequestBody ItemCreateDto item) {
        log.debug("Получен запрос POST userId: {}, /items: {}", userId, item);

        return itemMapper.toResponse(
                itemService.create(itemMapper.fromItemCreate(item), userId));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto update(@RequestHeader(name = USER_HEADER) Long userId,
                                  @Valid @RequestBody ItemUpdateDto item,
                                  @PathVariable Long id) {
        log.debug("Получен запрос PATCH userId: {}, /items/{}: {}", userId, id, item);

        return itemMapper.toResponse(
                itemService.update(itemMapper.fromItemUpdate(item), id, userId));
    }

    @GetMapping("/{id}") // ?
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto findById(@PathVariable Long id) {
        log.debug("Получен запрос GET /items/{}", id);

        return itemMapper.toResponse(
                itemService.findById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemResponseDto> findAll(
            @RequestHeader(name = USER_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer fromIndex,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен запрос GET /items");

        return itemService.findAll(userId, fromIndex, size).stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        log.debug("Получен запрос DELETE /items/{}", id);

        itemService.deleteById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemResponseDto> search(
            @RequestParam String text,
            @RequestParam(name = "from", defaultValue = "0") Integer fromIndex,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен запрос GET /search?text={}", text);

        return itemService.search(text, fromIndex, size).stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @PostMapping("/{itemId}/comment") // ?
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto addCommentary(@RequestHeader(name = USER_HEADER) Long userId,
                                            @PathVariable Long itemId,
                                            @Valid @RequestBody CommentCreateDto comment) {
        log.debug("Получен запрос POST userId: {}, /items/{}/comment", userId, itemId);

        return commentMapper.toResponse(
                itemService.addCommentary(userId, itemId, commentMapper.fromCommentCreate(comment)));
    }
}
