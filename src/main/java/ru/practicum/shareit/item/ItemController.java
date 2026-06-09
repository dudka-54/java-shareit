package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @RequestBody CreateItemRequest item) {

    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer id,
                                             @RequestBody CreateItemRequest item) {

    }

    @GetMapping
    public ResponseEntity<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer itemId) {

    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {

    }

    @GetMapping("/items/search?text={text}")
    public ResponseEntity<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable String text) {

    }
}
