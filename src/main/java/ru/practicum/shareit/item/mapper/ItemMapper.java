package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static Item mapToItem(CreateItemRequest request) {
        if (request == null) {
            return null;
        }
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item, User owner, ItemRequest request) {
        if (item == null) {
            return null;
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(UserMapper.toUserDto(owner))
                .request(request != null ? ItemRequestMapper.toDto(request, owner) : null)
                .build();
    }

}
