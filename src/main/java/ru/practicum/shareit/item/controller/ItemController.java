package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @RequestBody CreateItemRequest item) {
        log.info("POST /items - добавление новой вещи. userId={}, item={}", userId, item);
        ItemDto created = itemService.addNewItem(userId, item);
        log.info("POST /items - вещь успешно создана. itemId={}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> patchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer itemId,
                                             @RequestBody CreateItemRequest item) {
        log.info("PATCH /items/{} - обновление вещи. userId={}, updateData={}", itemId, userId, item);
        ItemDto updated = itemService.patchItem(userId, itemId, item);
        log.info("PATCH /items/{} - вещь успешно обновлена. updatedFields={}", itemId, updated);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer itemId) {
        log.info("GET /items/{} - получение вещи. userId={}", itemId, userId);
        ItemDto item = itemService.getItem(userId, itemId);
        log.info("GET /items/{} - вещь успешно получена: {}", itemId, item);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("GET /items - получение всех вещей пользователя. userId={}", userId);
        List<ItemDto> items = itemService.getItems(userId);
        log.info("GET /items - получено {} вещей пользователя userId={}", items.size(), userId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer itemId) {
        log.info("DELETE /items/{} - удаление вещи. userId={}", itemId, userId);
        itemService.delete(userId, itemId);
        log.info("DELETE /items/{} - вещь успешно удалена пользователем userId={}", itemId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestParam String text) {
        log.info("GET /items/search - поиск вещей. userId={}, searchText='{}'", userId, text);
        List<ItemDto> items = itemService.searchItem(userId, text);
        log.info("GET /items/search - найдено {} вещей по запросу '{}'", items.size(), text);
        return ResponseEntity.ok(items);
    }
}