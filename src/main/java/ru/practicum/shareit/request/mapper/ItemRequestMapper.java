package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request, User requestor) {
        if (request == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(UserMapper.toUserDto(requestor))
                .created(request.getCreated())
                .build();
    }
}
