package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface ItemRequestService {
    public ItemRequestDto getRequestOnId(Integer id, Integer ownerId);

    public Collection<ItemRequestDto> findAll(Integer ownerId);

    public UserDto save(CreateItemRequestDto newRequest, Integer ownerId);

    public UserDto update(CreateItemRequestDto request, Integer ownerId);

    public void delete(Integer id, Integer ownerId);
}
