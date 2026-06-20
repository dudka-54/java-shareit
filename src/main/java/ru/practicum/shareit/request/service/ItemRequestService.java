package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto getRequestOnId(Long id, Long ownerId);

    Collection<ItemRequestDto> findAll(Long ownerId);

    ItemRequestDto save(CreateUpdateItemRequestDto newRequest, Long ownerId);

    ItemRequestDto update(CreateUpdateItemRequestDto request, Long ownerId);

    void delete(Long id, Long ownerId);
}
