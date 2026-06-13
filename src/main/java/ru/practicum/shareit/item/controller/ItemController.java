package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
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
        ItemDto created = itemService.addNewItem(userId, item);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer itemId,
                                             @RequestBody CreateItemRequest item) {
        ItemDto updated = itemService.patchItem(userId, itemId, item);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer itemId) {
        ItemDto item = itemService.getItem(userId, itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<ItemDto> items = itemService.getItems(userId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer itemId) {
        itemService.delete(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestParam String text) {
        List<ItemDto> items = itemService.searchItem(userId, text);
        return ResponseEntity.ok(items);
    }
}