package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public ItemDto addNewItem(Integer userId, CreateItemRequest item);

    public ItemDto patchItem(Integer userId, Integer itemId, CreateItemRequest patchItem);

    public ItemDto getItem(Integer userId, Integer itemId);

    public List<ItemDto> getItems(Integer userId);

    public void delete(Integer id);

    public ItemDto searchItem(Integer userId, String text);
}
