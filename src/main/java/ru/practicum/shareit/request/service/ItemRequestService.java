package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto getRequestOnId(Long id, Long ownerId);

    Collection<ItemRequestDto> findAllWithoutOwner(Long ownerId);

    Collection<ItemRequestDto> findAllByOwner(Long ownerId);

    ItemRequestDto save(CreateItemRequestDto newRequest, Long ownerId);

    void delete(Long id, Long ownerId);
}
