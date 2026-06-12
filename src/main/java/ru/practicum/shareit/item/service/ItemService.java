package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Integer userId, CreateItemRequest item);

    ItemDto patchItem(Integer userId, Integer itemId, CreateItemRequest patchItem);

    ItemDto getItem(Integer userId, Integer itemId);

    List<ItemDto> getItems(Integer userId);

    void delete(Integer userId, Integer itemId);

    List<ItemDto> searchItem(Integer userId, String text);
}
