package ru.practicum.shareit.user.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> findAll() {
        log.info("GET-запрос на получение всех пользователей");
        Collection<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserOnId(@PathVariable Integer id) {
        log.info("GET-запрос на получение пользователя с id={}", id);
        UserDto user = userService.getUserOnId(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST-запрос на создание пользователя: {}", request.getEmail());
        UserDto created = userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<UserDto> update(
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT-запрос на обновление пользователя с id={}", request.getId());
        UserDto updated = userService.update(request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE-запрос на удаление пользователя с id={}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
