package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto getRequestOnId(Integer id, Integer ownerId);

    Collection<ItemRequestDto> findAll(Integer ownerId);

    ItemRequestDto save(CreateUpdateItemRequestDto newRequest, Integer ownerId);

    ItemRequestDto update(CreateUpdateItemRequestDto request, Integer ownerId);

    void delete(Integer id, Integer ownerId);
}
