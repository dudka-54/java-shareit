package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    public UserDto getUserOnId(Integer id);

    public Collection<UserDto> findAll();

    public UserDto save(CreateUserRequest user);

    public UserDto update(UpdateUserRequest request);

    public void delete(Integer id);
}
