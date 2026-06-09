package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static Item mapToItem(CreateItemRequest createItemRequest) {
        if (createItemRequest == null) {
            return null;
        }
        Item item = new Item();
        item.setName(createItemRequest.getName());
        item.setDescription(createItemRequest.getDescription());
        item.setAvailable(createItemRequest.isAvailable());
        item.setRequestId(createItemRequest.getRequest());
        item.setOwnerId(createItemRequest.getOwner());
        return item;

    }

    public static ItemDto toItemDto(Item item, User owner, ItemRequest request) {
        if (request == null) {
            return null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                owner,
                request
        );
    }

}
