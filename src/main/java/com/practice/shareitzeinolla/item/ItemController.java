package com.practice.shareitzeinolla.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @PostMapping
    public Item create(
            @RequestHeader(name = USER_HEADER) int userId,
            @Valid @RequestBody Item item
    ) {

    }
}
