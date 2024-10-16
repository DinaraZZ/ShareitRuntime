package com.practice.shareitzeinolla.item;

import com.practice.shareitzeinolla.item.dto.ItemCreateDto;
import com.practice.shareitzeinolla.item.dto.ItemMapper;
import com.practice.shareitzeinolla.item.dto.ItemResponseDto;
import com.practice.shareitzeinolla.item.dto.ItemUpdateDto;
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
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(@RequestHeader(name = USER_HEADER) int userId,
                                  @Valid @RequestBody ItemCreateDto item) {
        log.debug("Получен запрос POST userId: {}, /items: {}", userId, item);

        return itemMapper.toResponse(
                itemService.create(itemMapper.fromItemCreate(item), userId));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto update(@RequestHeader(name = USER_HEADER) int userId,
                                  @Valid @RequestBody ItemUpdateDto item,
                                  @PathVariable int id) {
        log.debug("Получен запрос PATCH userId: {}, /items/{}: {}", userId, id, item);

        return itemMapper.toResponse(
                itemService.update(itemMapper.fromItemUpdate(item), id, userId));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto findById(@PathVariable int id) {
        log.debug("Получен запрос GET /items/{}", id);

        return itemMapper.toResponse(
                itemService.findById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemResponseDto> findAll() {
        log.debug("Получен запрос GET /items");

        return itemService.findAll().stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable int id) {
        log.debug("Получен запрос DELETE /items/{}", id);

        itemService.deleteById(id);

    }
}
