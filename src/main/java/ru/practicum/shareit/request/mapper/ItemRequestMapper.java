package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.Optional;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(request.getItems().stream()
                        .map(ItemMapper::toItemShortDto)
                        .toList())
                .build();
    }
}
