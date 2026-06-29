package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CreateItemRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestService.save(requestDto, userId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestDto>> getMyRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.findAllByOwner(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> getAllOtherRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.findAllWithoutOwner(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.getRequestOnId(requestId, userId));
    }
}
