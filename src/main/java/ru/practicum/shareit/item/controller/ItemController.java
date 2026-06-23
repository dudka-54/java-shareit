package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
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
    public ResponseEntity<ItemDto> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody CreateItemRequest item) {
        ItemDto created = itemService.addNewItem(userId, item);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody CreateItemRequest item) {
        ItemDto updated = itemService.patchItem(userId, itemId, item);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{itemId}")     //и бронирования и комментарии
    public ResponseEntity<ItemBookingCommentDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long itemId) {
        ItemBookingCommentDto item = itemService.getItem(userId, itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping     //бронирование без комментариев
    public ResponseEntity<List<ItemBookingDto>> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemBookingDto> items = itemService.getItems(userId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search") //без бронирований и без комментариев
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam String text) {
        List<ItemDto> items = itemService.searchItem(userId, text);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam Long id,
                                                 @RequestBody CommentRequest request) {
        CommentDto commentDto = itemService.addComment(userId, id, request);
        return ResponseEntity.ok(commentDto);
    }
}