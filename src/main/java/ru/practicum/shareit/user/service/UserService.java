package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto getUserOnId(Long id);

    Collection<UserDto> findAll();

    UserDto save(CreateUserRequest user);

    UserDto patch(UpdateUserRequest request, Long userId);

    void delete(Long id);
}
