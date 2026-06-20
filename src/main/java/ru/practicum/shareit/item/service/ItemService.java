package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, CreateItemRequest item);

    ItemDto patchItem(Long userId, Long itemId, CreateItemRequest patchItem);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    void delete(Long userId, Long itemId);

    List<ItemDto> searchItem(Long userId, String text);
}
